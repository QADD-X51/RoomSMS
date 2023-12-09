namespace RoomSMSserver.Database.CRUDs
{
    public interface ICRUD<T,Y>
        where T : class
    {
        public void Add(T Added);
        public void Update(Y updatedPrimaryKey,T Updated);
        public void ChangeDeletedState(Y updatedPrimaryKey,bool isDeleted);
    }
}
