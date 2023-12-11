using RoomSMSserver.Database.Entities;

namespace RoomSMSserver.Database.CRUDs
{
    public class MemberCRUD : ICRUD<Member, int>
    {
        ProiectPdmContext dbContext;
        public MemberCRUD(ProiectPdmContext dbContext)
        {
            this.dbContext = dbContext;
        }
        public void Add(Member addedMember)
        {
            var members = dbContext.Members;
            var foundMember = members.SingleOrDefault(x => x.IdUser == addedMember.IdUser && 
                x.IdRoom == addedMember.IdRoom);
            if (foundMember == null)
            {
                dbContext.Members.Add(addedMember);
                dbContext.SaveChanges();
            }
            if (foundMember != null)
            {
                if(foundMember.IsDeleted == true) 
                {
                    addedMember.IsDeleted = false;
                    Update(addedMember.Id, addedMember);
                }
            }
        }
        public void Update(int id, Member updatedMember)
        {
            var members = dbContext.Members;
            dbContext.Entry(members.SingleOrDefault(x => x.Id == id)).CurrentValues.SetValues(updatedMember);
            dbContext.SaveChanges();
        }
        public void ChangeDeletedState(int id, bool isDeleted)
        {
            var members = dbContext.Members;
            Member updatedMember = members.SingleOrDefault(x => x.Id == id);
            updatedMember.IsDeleted = isDeleted;
            dbContext.Entry(members.SingleOrDefault(x => x.Id == id)).CurrentValues.SetValues(updatedMember);
            dbContext.SaveChanges();
        }
        public List<int> GetRoomIDsOfMember(int idUser)
        {
            var members = dbContext.Members;
            List<int> roomIDs = members.Where(member => member.IdUser == idUser && member.IsDeleted == false)
                .Select(member => member.IdRoom).ToList();
            return roomIDs;
        }
        public List<Member> GetMembersInRoom(int idRoom)
        {
            var members = dbContext.Members;
            List<Member> foundMembers = members.Where(member => member.IdRoom == idRoom && member.IsDeleted == false)
                .ToList();
            return foundMembers;
        }
        public int CountMembersInRoom(int idRoom)
        {
            var members = dbContext.Members;
            int counter = members.Where(member => member.IdRoom == idRoom && member.IsDeleted == false).Count();
            return counter;
        }
        public string GetMemberRole(int idUser,int idRoom)
        {
            var members = dbContext.Members;
            Member foundMember = members.SingleOrDefault(x => x.IdUser == idUser && x.IdRoom == idRoom && x.IsDeleted == false);
            return foundMember != null ? foundMember.Role : "";
        }
        public int GetMemberId(int idUser,int idRoom)
        {
            var members = dbContext.Members;
            Member foundMember = members.SingleOrDefault(x => x.IdUser == idUser && x.IdRoom == idRoom && x.IsDeleted == false);
            return foundMember != null ? foundMember.Id : -1;
        }
        public Member GetMemberById(int idMember)
        {
            List<Member> members = dbContext.Members.ToList();
            return members.SingleOrDefault(x => x.Id == idMember && x.IsDeleted == false);
        }
    }
}
