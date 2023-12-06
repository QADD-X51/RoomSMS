﻿using System.Security.Cryptography;

namespace RoomSMSserver.Services
{
    public interface IUserAuthorizationService
    {
        string HashPassword(string password);
        bool VerifyHashedPassword(string hashedPassword, string password);
    }

    public class UserAuthorizationService : IUserAuthorizationService
    {
        private int PBKDF2IterCount = 1000;
        private int PBKDF2SubkeyLength = 256 / 8;
        private int SaltSize = 128 / 8;

        public string HashPassword(string password)
        {
            if (password == null)
                throw new ArgumentNullException("PASSWORD_CANNOT_BE_NULL");

            byte[] salt;
            byte[] subkey;

            using (var deriveBytes = new Rfc2898DeriveBytes(password, SaltSize, PBKDF2IterCount))
            {
                salt = deriveBytes.Salt;
                subkey = deriveBytes.GetBytes(PBKDF2SubkeyLength);
            }

            var outputBytes = new byte[1 + SaltSize + PBKDF2SubkeyLength];
            Buffer.BlockCopy(salt, 0, outputBytes, 1, SaltSize);
            Buffer.BlockCopy(subkey, 0, outputBytes, 1 + SaltSize, PBKDF2SubkeyLength);

            string parola = Convert.ToBase64String(outputBytes);

            return Convert.ToBase64String(outputBytes);
        }

        public bool VerifyHashedPassword(string hashedPassword, string password)
        {
            if (hashedPassword == null)
                return false;

            if (password == null)
                throw new ArgumentNullException(nameof(password));

            var hashedPasswordBytes = Convert.FromBase64String(hashedPassword);

            if (hashedPasswordBytes.Length != (1 + SaltSize + PBKDF2SubkeyLength) || hashedPasswordBytes[0] != 0x00)
                return false;

            var salt = new byte[SaltSize];
            Buffer.BlockCopy(hashedPasswordBytes, 1, salt, 0, SaltSize);

            var storedSubkey = new byte[PBKDF2SubkeyLength];
            Buffer.BlockCopy(hashedPasswordBytes, 1 + SaltSize, storedSubkey, 0, PBKDF2SubkeyLength);

            byte[] generatedSubkey;
            using (var deriveBytes = new Rfc2898DeriveBytes(password, salt, PBKDF2IterCount))
            {
                generatedSubkey = deriveBytes.GetBytes(PBKDF2SubkeyLength);
            }

            return ByteArraysEqual(storedSubkey, generatedSubkey);
        }

        private bool ByteArraysEqual(byte[] a, byte[] b)
        {
            if (ReferenceEquals(a, b))
                return true;

            if (a == null || b == null || a.Length != b.Length)
                return false;

            var areSame = true;

            for (var i = 0; i < a.Length; i++)
                areSame &= (a[i] == b[i]);

            return areSame;
        }
    }
}
