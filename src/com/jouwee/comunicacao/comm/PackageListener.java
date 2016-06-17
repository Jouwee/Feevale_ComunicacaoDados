package com.jouwee.comunicacao.comm;

public interface PackageListener {

    public abstract void packageSent(Package pack);
    public abstract void packageReceived(Package pack);
    public abstract void bytesRead(byte[] bytes);
    
}
