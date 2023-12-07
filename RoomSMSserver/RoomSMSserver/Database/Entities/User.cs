using System;
using System.Collections.Generic;

namespace RoomSMSserver.Database.Entities;

public partial class User
{
    public int Id { get; set; }

    public string Username { get; set; } = null!;

    public string Password { get; set; } = null!;

    public string Email { get; set; } = null!;

    public bool IsDeleted { get; set; }

    public virtual ICollection<Member> Members { get; set; } = new List<Member>();

    public virtual ICollection<RoomMessage> RoomMessages { get; set; } = new List<RoomMessage>();

    public virtual ICollection<Room> Rooms { get; set; } = new List<Room>();
}
