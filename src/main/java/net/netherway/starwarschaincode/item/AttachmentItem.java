package net.netherway.starwarschaincode.item;

public interface AttachmentItem {
    AttachmentType getAttachmentType();

    enum AttachmentType {
        SCOPE, STOCK, BARREL
    }
}