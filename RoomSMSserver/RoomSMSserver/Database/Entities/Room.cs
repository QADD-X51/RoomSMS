using System;
using System.Collections.Generic;

namespace RoomSMSserver.Database.Entities;

public partial class Room
{
    public int Id { get; set; }

    public string Name { get; set; } = null!;

    public int IdOwner { get; set; }

    public bool IsDeleted { get; set; }

    public virtual User IdOwnerNavigation { get; set; } = null!;

    public virtual ICollection<Member> Members { get; set; } = new List<Member>();

    public virtual ICollection<RoomMessage> RoomMessages { get; set; } = new List<RoomMessage>();
}
