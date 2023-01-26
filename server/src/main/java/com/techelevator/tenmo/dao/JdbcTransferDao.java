package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    private JdbcAccountDao jdbcAccountDao;
    private JdbcUserDao jdbcUserDao;
    public JdbcTransferDao(JdbcTemplate jdbcTemplate, JdbcAccountDao jdbcAccountDao, JdbcUserDao jdbcUserDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAccountDao = jdbcAccountDao;
        this.jdbcUserDao = jdbcUserDao;
    }

    // I dont think we use this method anywhere, so we might not need it anymore **************************
    @Override
    public int findTransferId(int userId) {
        int transfer = 0;
        String sql = "SELECT transfer_id FROM transfer WHERE sender_id = ? OR receiver_id = ? ;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId,userId);
        if(results.next()){
            transfer=results.getInt("transfer_id");
        }
        return transfer;
    }

    @Override
    public List<Transfer> getAllPendingUserTransfers(int userId) {
        List<Transfer> pendingList = new ArrayList<>();
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_type, transfer_amount, Transfer_status " +
                     "FROM transfer " +
                     "WHERE (sender_id = ? OR receiver_id = ?) AND transfer_status ILIKE 'pending';";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while(results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            pendingList.add(transfer);
        }
        return pendingList;
    }

    // this method updates the requestTransfer below to either Approved or Rejected, depending on the input by the receiving user
    // the status will stay pending if the amount requested is negative, 0,
    @Override
    public boolean updateRequestTransfer(int receiverId, int transferId, String transferStatus) {
        Transfer transfer = getTransfer(jdbcUserDao.findByUserId(receiverId), transferId);

        // check to see if the transfer amount is less than the receiving users balance
        if (transfer.getTransferAmount() <= jdbcAccountDao.getBalance(receiverId)) {
            if (transferStatus.equalsIgnoreCase("rejected")) {

                // We needed to include the receiver_id in the WHERE statement in order to make sure the logged-in user ->
                // in the controller class is the one approving or rejecting this request
                String sql = "UPDATE transfer SET transfer_status = ? WHERE transfer_id = ? AND receiver_id = ?;";
                jdbcTemplate.update(sql, transferStatus, transferId, receiverId);

                System.out.println("Transfer Request was rejected by receiving user.");
                return true;
            }
            else if (transferStatus.equalsIgnoreCase("approved")) {

                String sql = "UPDATE transfer SET transfer_status = ? WHERE transfer_id = ? AND receiver_id = ?;";
                jdbcTemplate.update(sql, transferStatus, transferId, receiverId);
                // update the balances for the sender and the receiver
                jdbcAccountDao.addToBalance(transfer.getTransferAmount(), transfer.getSenderId());
                jdbcAccountDao.subtractToBalance(transfer.getTransferAmount(), receiverId);

                System.out.println("Transfer Request was approved by receiving user.");
                return true;
            }
            else {
                System.out.println("Something went wrong and request is still in pending.");
                return false;
            }
        }
        else {
            // we reject the request if the amount requested is more than the current balance for the receiver
            String sql = "UPDATE transfer SET transfer_status = 'rejected' WHERE transfer_id = ? AND receiver_id = ?;";
            jdbcTemplate.update(sql, transferId, receiverId);

            System.out.println("The transfer amount is more than users current balance.");
            return false;
        }
    }

    // this method requests a transfer from a user (the sender is the person sending the request and the receiver is the one getting the request)
    // this means the receiver will be the person sending money to the person who initiated the request (kinda backwards but thats how venmo kinda works)
    @Override
    public boolean requestTransfer(int senderId, int receiverId, double sendingAmount) {
        // check to make sure the sender and the receiver are different users
        if (senderId == receiverId) {
            System.out.println("Send user cannot be the same as Receiving user.");
            return false;
        }
        // check to make sure sending amount is over 0 and not negative (this will be compared to the receivers balance in another method)
        if (sendingAmount > 0) { // (sendingAmount <= jdbcAccountDao.getBalance(senderId) && sendingAmount > 0)
            String sql = "INSERT INTO transfer (sender_id, receiver_id, transfer_type, transfer_amount, transfer_status) " +
                         "VALUES (?,?,'request',?,'pending');";
            jdbcTemplate.update(sql, senderId, receiverId, sendingAmount);

            System.out.println("Transfer Request has been sent.");
            return true;
        }
        else {
            System.out.println("Transfer Request amount cannot be 0 or negative.");
            return false;
        }
    }

    // this method allows a user to send money to a receiving user (this is approved automatically since the sender is giving away their money)
    @Override
    public boolean sendTransfer(int senderId, int receiverId, double sendingAmount) {
        // check to make sure the sender and the receiver are different users
        if (senderId == receiverId){
            System.out.println("Send user cannot be the same as Receiving user.");
            return false;
        }
        // check to make sure sending amount is over 0 and not negative compare to the senders balance
        if (sendingAmount <= jdbcAccountDao.getBalance(senderId) && sendingAmount > 0){
            String sql = "INSERT INTO transfer (sender_id, receiver_id, transfer_type, transfer_amount, transfer_status) " +
                         "VALUES (?,?,'send',?, 'approved'); ";
            jdbcTemplate.update(sql, senderId, receiverId, sendingAmount);
            // update the balances for the sender and the receiver
            jdbcAccountDao.addToBalance(sendingAmount, receiverId);
            jdbcAccountDao.subtractToBalance(sendingAmount, senderId);

            System.out.println("Transfer Request has been sent.");
            return true;
        }
        else {
            System.out.println("Transfer Request amount is not allowed");
            return false;
        }

    }

    // this method creates a list of all transfers related to the userId, approved, reject, or pending
    @Override
    public List<Transfer> seeAllTransfers(int userId) {
        List<Transfer> list = new ArrayList<>();
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_type, transfer_amount, Transfer_status " +
                     "FROM transfer " +
                     "WHERE sender_id = ? OR receiver_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (results.next()) {
            Transfer transfer = mapRowToTransfer(results);
            list.add(transfer);
        }
        return list;
    }

    // this method allows a user to get a specific transfer via a transferId
    @Override
    public Transfer getTransfer(User user, int transferId) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, sender_id, receiver_id, transfer_type, transfer_amount, Transfer_status " +
                     "FROM transfer " +
                     "WHERE (sender_id = ? OR receiver_id = ?) AND transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId(), user.getId(), transferId);
        if (results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    private Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setSenderId(rowSet.getInt("sender_id"));
        transfer.setReceiverId(rowSet.getInt("receiver_id"));
        transfer.setTransferType(rowSet.getString("transfer_type"));
        transfer.setTransferAmount(rowSet.getDouble("transfer_amount"));
        transfer.setTransferStatus(rowSet.getString("transfer_status"));
        return transfer;
    }
}
