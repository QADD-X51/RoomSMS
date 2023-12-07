using System;
using System.Collections.Generic;

namespace RoomSMSserver.Database.Entities;

public partial class RoomMessage
{
    public int Id { get; set; }

    public string Message { get; set; } = null!;

    public int IdUser { get; set; }

    public int IdRoom { get; set; }

    public DateTime Date { get; set; }

    public bool IsDeleted { get; set; }

    public virtual Room IdRoomNavigation { get; set; } = null!;

    public virtual User IdUserNavigation { get; set; } = null!;
}
