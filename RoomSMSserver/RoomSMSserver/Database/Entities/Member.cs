using System;
using System.Collections.Generic;

namespace RoomSMSserver.Database.Entities;

public partial class Member
{
    public int Id { get; set; }

    public int IdUser { get; set; }

    public int IdRoom { get; set; }

    public string Role { get; set; } = null!;

    public bool IsDeleted { get; set; }

    public virtual Room IdRoomNavigation { get; set; } = null!;

    public virtual User IdUserNavigation { get; set; } = null!;
}
