﻿using RoomSMSserver.Database.Entities;

namespace RoomSMSserver.Database.CRUDs
{
    public class UserCRUD : ICRUD<User, string>
    {
        ProiectPdmContext dbContext;
        public UserCRUD(ProiectPdmContext dbContext)
        {
            this.dbContext = dbContext;
        }
        public void Add(User addedUser)
        {
            dbContext.Users.Add(addedUser);
            dbContext.SaveChanges();
        }
        public void Update(string email, User updatedUser)
        {
            var users = dbContext.Users;
            dbContext.Entry(users.SingleOrDefault(x => x.Email == email)).CurrentValues.SetValues(updatedUser);
            dbContext.SaveChanges();
        }
        public void ChangeDeletedState(string email, bool isDeleted)
        {
            var users = dbContext.Users;
            User updatedUser = users.SingleOrDefault(x => x.Email == email);
            updatedUser.IsDeleted = isDeleted;
            dbContext.Entry(users.SingleOrDefault(x => x.Email == email)).CurrentValues.SetValues(updatedUser);
            dbContext.SaveChanges();
        }
        public List<string> GetAllUsersEmails()
        {
            List<User> users = dbContext.Users.ToList();
            return users.FindAll(x => x.IsDeleted == false).Select(x => x.Email).ToList();
        }
        public User GetUserByUsername(string email)
        {
            List<User> users = dbContext.Users.ToList();
            return users.SingleOrDefault(x => x.Email == email);
        }
        public void ChangePassword(string email, string hashedNewPassword)
        {
            var users = dbContext.Users;
            User updatedUser = users.SingleOrDefault(x => x.Email == email);
            updatedUser.Password = hashedNewPassword;
            dbContext.Entry(users.SingleOrDefault(x => x.Email == email)).CurrentValues.SetValues(updatedUser);
            dbContext.SaveChanges();
        }
    }
}
