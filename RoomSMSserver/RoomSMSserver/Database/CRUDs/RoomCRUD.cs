using RoomSMSserver.Database.Entities;

namespace RoomSMSserver.Database.CRUDs
{
    public class RoomCRUD : ICRUD<Room, int>
    {
        ProiectPdmContext dbContext;
        public RoomCRUD(ProiectPdmContext dbContext)
        {
            this.dbContext = dbContext;
        }
        public void Add(Room addedRoom)
        {
            dbContext.Rooms.Add(addedRoom);
            dbContext.SaveChanges();
        }
        public void Update(int id, Room updatedRoom)
        {
            var rooms = dbContext.Rooms;
            dbContext.Entry(rooms.SingleOrDefault(x => x.Id == id)).CurrentValues.SetValues(updatedRoom);
            dbContext.SaveChanges();
        }
        public void ChangeDeletedState(int id, bool isDeleted)
        {
            var rooms = dbContext.Rooms;
            Room updatedRoom = rooms.SingleOrDefault(x => x.Id == id);
            updatedRoom.IsDeleted = isDeleted;
            dbContext.Entry(rooms.SingleOrDefault(x => x.Id == id)).CurrentValues.SetValues(updatedRoom);
            dbContext.SaveChanges();
        }
    }
}
