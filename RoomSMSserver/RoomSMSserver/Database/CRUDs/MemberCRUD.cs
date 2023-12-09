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
            dbContext.Members.Add(addedMember);
            dbContext.SaveChanges();
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
    }
}
