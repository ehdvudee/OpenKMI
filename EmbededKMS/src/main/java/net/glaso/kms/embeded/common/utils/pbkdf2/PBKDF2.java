package net.glaso.kms.embeded.common.utils.pbkdf2;

public interface PBKDF2 {
	public abstract byte[] deriveKey(byte[] P, byte[] salt, int iterationCount, int derivedKeyLength);
}