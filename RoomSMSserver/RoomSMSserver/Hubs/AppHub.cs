using Microsoft.AspNetCore.SignalR;
using RoomSMSserver.Database.CRUDs;
using RoomSMSserver.Database.Entities;
using RoomSMSserver.Services;
using RoomSMSserver.Models;
using System.Collections.Concurrent;
using System.ComponentModel;

namespace RoomSMSserver.Hubs
{
    public enum Roles
    {
        Member,
        Admin,
        Owner
    }
    public class AppHub : Hub
    {
        private ProiectPdmContext dbContext;
        private IUserAuthorizationService _authorization;

        public AppHub(IUserAuthorizationService authorization, ProiectPdmContext dbContext)
        {
            this._authorization = authorization;
            this.dbContext = dbContext;
        }

        public async Task<string> RegisterUser(string username, string email, string password)
        {
            UserCRUD userCRUD = new UserCRUD(dbContext);
            if (username == null || email == null || password == null)
            {
                return "Null field received";
            }
            List<string> userEmails = userCRUD.GetAllUsersEmails();
            if (userEmails.Any(s => s == email))
            {
                return "Email already exists";
            }
            var userToAdd = new User
            {
                Username = username,
                Email = email,
                Password = _authorization.HashPassword(password)
            };
            userCRUD.Add(userToAdd);
            await Task.CompletedTask;
            return "Ok";
        }

        public async Task<StringIntegerModel> Login(string email, string password)
        {
            UserCRUD userCRUD = new UserCRUD(dbContext);
            if (email == null || password == null)
            {
                return new StringIntegerModel() { message = "No", integer = -1 };
            }
            var foundUser = userCRUD.GetUserByEmail(email);
            if (foundUser == null)
            {
                return new StringIntegerModel() { message = "No", integer = -1 };
            }
            var samePassword = _authorization.VerifyHashedPassword(foundUser.Password, password);
            if (!samePassword)
            {
                return new StringIntegerModel() { message = "No", integer = -1 };
            }
            await Task.CompletedTask;
            return new StringIntegerModel() { message = "Ok", integer = foundUser.Id };
        }
        public async Task<string> GetUsernameById(int id)
        {
            UserCRUD userCRUD = new UserCRUD(dbContext);
            var foundUser = userCRUD.GetUserById(id);
            if (foundUser == null)
            {
                return "";
            }
            await Task.CompletedTask;
            return foundUser.Username;
        }
        public async Task<string> CreateRoom(int id, string roomName)
        {
            UserCRUD userCRUD = new UserCRUD(dbContext);
            RoomCRUD roomCrud = new RoomCRUD(dbContext);
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            var foundUser = userCRUD.GetUserById(id);
            if (foundUser == null)
            {
                return "No";
            }
            var roomToAdd = new Room
            {
                Name = roomName,
                IdOwner = id
            };
            var idRoom = roomCrud.Add(roomToAdd);
            var memberToAdd = new Member
            {
                IdRoom = idRoom,
                IdUser = foundUser.Id,
                Role = Roles.Owner.ToString()
            };
            memberCRUD.Add(memberToAdd);
            await Task.CompletedTask;
            return "Ok";
        }
        public async Task<List<RoomInfoModel>> GetRoomsWithGivenMember(int memberId)
        {
            UserCRUD userCRUD = new UserCRUD(dbContext);
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            List<int> roomIDs = memberCRUD.GetRoomIDsOfMember(memberId);
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            List<RoomInfoModel> roomInfos = roomIDs.Select(foundRoomId =>
                new RoomInfoModel(foundRoomId, roomCRUD.GetRoomName(foundRoomId),
                    userCRUD.GetUserById(roomCRUD.GetRoomById(foundRoomId).IdOwner).Username)).ToList();
            //Console.WriteLine("Room Count: " + roomInfos.Count.ToString());
            await Task.CompletedTask;
            return roomInfos;
        }
        public async Task<StringIntegerModel> GetRoomNameAndMembersCount(int roomId)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            var foundRoom = roomCRUD.GetRoomById(roomId);
            if (foundRoom == null)
            {
                return new StringIntegerModel() { message = "", integer = 0 };
            }
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            var counter = memberCRUD.CountMembersInRoom(roomId);
            await Task.CompletedTask;
            return new StringIntegerModel() { message = foundRoom.Name, integer = counter };
        }
        public async Task<List<MessageModel>> GetRoomMessages(int roomId)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            UserCRUD userCRUD = new UserCRUD(dbContext);
            RoomMessageCRUD roomMessageCRUD = new RoomMessageCRUD(dbContext);
            List<MessageModel> messages = new List<MessageModel>();
            var foundRoom = roomCRUD.GetRoomById(roomId);
            if (foundRoom == null)
            {
                return messages;
            }
            List<RoomMessage> roomMessages = roomMessageCRUD.GetAllRoomMessages(roomId);
            messages = roomMessages
                .Select(x => new MessageModel(x.Message, userCRUD.GetUserById(x.IdUser).Username, x.Date))
                .OrderBy(x => x.sentDate)
                .ToList();
            await Task.CompletedTask;
            return messages;
        }
        public async Task<string> SendMessage(int idUser, int idRoom, string message)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            UserCRUD userCRUD = new UserCRUD(dbContext);
            RoomMessageCRUD roomMessageCRUD = new RoomMessageCRUD(dbContext);
            var foundUser = userCRUD.GetUserById(idUser);
            if (foundUser == null)
            {
                return "User not found";
            }
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return "Room not found";
            }
            if (message == string.Empty)
            {
                return "Message is empty";
            }
            var messageToAdd = new RoomMessage
            {
                Message = message,
                IdUser = idUser,
                IdRoom = idRoom,
                Date = DateTime.Now
            };
            roomMessageCRUD.Add(messageToAdd);
            var messageToSend = new MessageModel(message, userCRUD.GetUserById(idUser).Username, messageToAdd.Date);
            await Clients.All.SendAsync("SendMessageToClients", idRoom, messageToSend);
            return "Ok";
        }
        public async Task<string> RemoveMemberFromRoom(int idUser, int idRoom)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            UserCRUD userCRUD = new UserCRUD(dbContext);
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            var foundUser = userCRUD.GetUserById(idUser);
            if (foundUser == null)
            {
                return "No";
            }
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return "No";
            }
            var memberId = memberCRUD.GetMemberId(idUser, idRoom);
            if (memberId == -1)
            {
                return "No";
            }
            memberCRUD.ChangeDeletedState(memberId, true);
            await Task.CompletedTask;
            return "Ok";
        }
        public async Task<string> RemoveRoom(int idRoom)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return "No";
            }
            roomCRUD.ChangeDeletedState(idRoom, true);
            await Task.CompletedTask;
            return "Ok";
        }
        public async Task<string> AddMember(int idRoom, string email)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            UserCRUD userCRUD = new UserCRUD(dbContext);
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            var foundUser = userCRUD.GetUserByEmail(email);
            if (foundUser == null)
            {
                return "No";
            }
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return "No";
            }
            var foundMemberId = memberCRUD.GetMemberId(foundUser.Id, foundRoom.Id);
            if (foundMemberId != -1)
            {
                return "No";
            }
            var memberToAdd = new Member
            {
                IdRoom = idRoom,
                IdUser = foundUser.Id,
                Role = Roles.Member.ToString()
            };
            memberCRUD.Add(memberToAdd);
            await Task.CompletedTask;
            return "Ok";
        }
        public async Task<string> ChangeRoomName(int idRoom, string newRoomName)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return "No";
            }
            foundRoom.Name = newRoomName;
            roomCRUD.Update(idRoom, foundRoom);
            await Task.CompletedTask;
            return "Ok";
        }
        public async Task<List<MemberInfoModel>> GetRoomUsersRoles(int idRoom)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            UserCRUD userCRUD = new UserCRUD(dbContext);
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            List<MemberInfoModel> memberInfos = new List<MemberInfoModel>();
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return memberInfos;
            }
            var foundMembers = memberCRUD.GetMembersInRoom(idRoom);
            memberInfos = foundMembers.Select(x => new MemberInfoModel(x.IdUser,
                userCRUD.GetUserById(x.IdUser).Username, x.Role)).ToList();
            await Task.CompletedTask;
            return memberInfos;
        }
        public async Task<string> ChangeMemberRole(int idUser, int idRoom)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            UserCRUD userCRUD = new UserCRUD(dbContext);
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            var foundUser = userCRUD.GetUserById(idUser);
            if (foundUser == null)
            {
                return "No";
            }
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return "No";
            }
            var foundMemberId = memberCRUD.GetMemberId(foundUser.Id, foundRoom.Id);
            if (foundMemberId == -1)
            {
                return "No";
            }
            var foundMember = memberCRUD.GetMemberById(foundMemberId);
            if(foundMember.Role == Roles.Owner.ToString())
            {
                return "No";
            }
            else if (foundMember.Role == Roles.Member.ToString())
            {
                foundMember.Role = Roles.Admin.ToString();
            }
            else if(foundMember.Role == Roles.Admin.ToString())
            {
                foundMember.Role = Roles.Member.ToString();
            }
            memberCRUD.Update(foundMemberId, foundMember);
            await Task.CompletedTask;
            return "Ok";
        }
        public async Task<string> GetMemberRole(int idUser, int idRoom)
        {
            RoomCRUD roomCRUD = new RoomCRUD(dbContext);
            UserCRUD userCRUD = new UserCRUD(dbContext);
            MemberCRUD memberCRUD = new MemberCRUD(dbContext);
            var foundUser = userCRUD.GetUserById(idUser);
            if (foundUser == null)
            {
                return "";
            }
            var foundRoom = roomCRUD.GetRoomById(idRoom);
            if (foundRoom == null)
            {
                return "";
            }
            var foundMemberId = memberCRUD.GetMemberId(foundUser.Id, foundRoom.Id);
            if (foundMemberId == -1)
            {
                return "";
            }
            var foundMember = memberCRUD.GetMemberById(foundMemberId);
            await Task.CompletedTask;
            return foundMember.Role;
        }
    }
}
