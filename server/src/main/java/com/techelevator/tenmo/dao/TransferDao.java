package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface TransferDao {

    List<Transfer> getAllPendingUserTransfers(int userId);

    boolean updateRequestTransfer(int receiverId, int transferId, String transferStatus);

    boolean requestTransfer(int senderId, int receiverId, double sendingAmount);

    boolean sendTransfer(int senderId, int receiverId, double sendingAmount);

    int findTransferId(int userId);

    List<Transfer> seeAllTransfers(int userId);

    Transfer getTransfer(User user, int transferId);
}
