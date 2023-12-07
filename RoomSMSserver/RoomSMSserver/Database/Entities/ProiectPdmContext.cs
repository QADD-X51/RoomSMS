using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace RoomSMSserver.Database.Entities;

public partial class ProiectPdmContext : DbContext
{
    public ProiectPdmContext()
    {
    }

    public ProiectPdmContext(DbContextOptions<ProiectPdmContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Member> Members { get; set; }

    public virtual DbSet<Room> Rooms { get; set; }

    public virtual DbSet<RoomMessage> RoomMessages { get; set; }

    public virtual DbSet<User> Users { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        => optionsBuilder.UseSqlServer("Name=ConnectionStrings:DatabaseConnection");

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Member>(entity =>
        {
            entity.ToTable("Member");

            entity.Property(e => e.Role).HasMaxLength(20);

            entity.HasOne(d => d.IdRoomNavigation).WithMany(p => p.Members)
                .HasForeignKey(d => d.IdRoom)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_IdRoom_IdRoomMember");

            entity.HasOne(d => d.IdUserNavigation).WithMany(p => p.Members)
                .HasForeignKey(d => d.IdUser)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_IdUser_IdUserMember");
        });

        modelBuilder.Entity<Room>(entity =>
        {
            entity.ToTable("Room");

            entity.Property(e => e.Name).HasMaxLength(50);

            entity.HasOne(d => d.IdOwnerNavigation).WithMany(p => p.Rooms)
                .HasForeignKey(d => d.IdOwner)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_IdUser_IdOwnerRoom");
        });

        modelBuilder.Entity<RoomMessage>(entity =>
        {
            entity.ToTable("RoomMessage");

            entity.Property(e => e.Date).HasColumnType("date");
            entity.Property(e => e.Message).HasMaxLength(500);

            entity.HasOne(d => d.IdRoomNavigation).WithMany(p => p.RoomMessages)
                .HasForeignKey(d => d.IdRoom)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_IdRoom_IdRoomRoomMessage");

            entity.HasOne(d => d.IdUserNavigation).WithMany(p => p.RoomMessages)
                .HasForeignKey(d => d.IdUser)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_IdUser_IdUserRoomMessage");
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.ToTable("User");

            entity.HasIndex(e => e.Email, "Unique_Email").IsUnique();

            entity.Property(e => e.Email).HasMaxLength(50);
            entity.Property(e => e.Password).HasMaxLength(100);
            entity.Property(e => e.Username).HasMaxLength(50);
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
