package com.example.bankingmanagement.Controller;

import com.example.bankingmanagement.Models.Accounts;
import com.example.bankingmanagement.Models.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/hi")
    public String hello() {
        String hello = "Hello";
        return hello;
    }
    private static final String USERS_FILE_PATH = "C:\\Users\\prajw\\.vscode\\Nisarga's Project\\Bankingmangement\\bankingmanagement\\src\\main\\java\\com\\example\\bankingmanagement\\Models\\users.json";
    private static final String ACCOUNTS_FILE_PATH = "C:\\Users\\prajw\\.vscode\\Nisarga's Project\\Bankingmangement\\bankingmanagement\\src\\main\\java\\com\\example\\bankingmanagement\\Models\\accounts.json";
    //---------------------------------------------Login and Signup APIs Start-------------------------------------------//
    private final List<Users> users = new ArrayList<>();

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody Users newUser) {
        try {
            // Load existing users from the JSON file
            loadUsers();

            // Check if the username is already taken
            for (Users user : users) {
                if (user.getUsername().equals(newUser.getUsername())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
                }
            }

            // Set the ID for the new user
            Long nextId = getNextUserId();
            newUser.setId(nextId);

            // Add the new user to the list
            users.add(newUser);

            // Save the updated user list to the JSON file
            saveUsers();

            // Create a response payload with the userID and username
            // You can customize the response structure as per your requirements
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("userId", newUser.getId());
            responseBody.put("username", newUser.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during signup");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Users loginUser) {
        try {
            // Load existing users from the JSON file
            loadUsers();

            // Find the user with matching username and password
            for (Users user : users) {
                if (user.getUsername().equals(loginUser.getUsername()) && user.getPassword().equals(Users.hashPassword(loginUser.getPassword()))) {
                    // Create a response payload with the userID and username
                    // You can customize the response structure as per your requirements
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("userId", user.getId());
                    responseBody.put("username", user.getUsername());

                    return ResponseEntity.status(HttpStatus.OK).body(responseBody);
                }
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during login");
        }
    }
    // Helper method to load users from the JSON file
    private void loadUsers() throws IOException {
        File file = new File(USERS_FILE_PATH);

        // If the file exists, load the users
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            users.clear();
            users.addAll(mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Users.class)));
        }
    }

    // Helper method to save users to the JSON file
    private void saveUsers() throws IOException {
        File file = new File(USERS_FILE_PATH);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, users);
    }

    // Helper method to generate the next available user ID
    private Long getNextUserId() {
        Long maxId = 0L;
        for (Users user : users) {
            if (user.getId() > maxId) {
                maxId = user.getId();
            }
        }
        return maxId + 1;
    }

//---------------------------------------------Login and Signup APIs Finish-------------------------------------------//


//---------------------------------------------Create an Account-----------------------------------------------------//
    private final List<Accounts>  accounts = new ArrayList<>();
    @PostMapping("/createAccount")
    public ResponseEntity<Object> createAccount(@RequestBody Accounts newAccount) {
        try {
            // Load existing accounts from the JSON file
            loadAccounts();

            // Generate a new account ID
            Long nextId = getNextAccountId();
            newAccount.setId(nextId);
            // Add the new account to the list
            accounts.add(newAccount);

            // Save the updated account list to the JSON file
            saveAccounts();

            // Create a response payload with the account ID and other details
            // You can customize the response structure as per your requirements
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("accountId", newAccount.getId());
            responseBody.put("name", newAccount.getName());
            responseBody.put("phone", newAccount.getPhone());
            responseBody.put("address", newAccount.getAddress());
            responseBody.put("accountType", newAccount.getAccountType());
            responseBody.put("balance", newAccount.getBalance());
            responseBody.put("username", newAccount.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during account creation");
        }
    }

    // Helper method to load accounts from the JSON file
    private void loadAccounts() throws IOException {
        File file = new File(ACCOUNTS_FILE_PATH);

        // If the file exists, load the accounts
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            accounts.clear();
            accounts.addAll(mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Accounts.class)));
        }
    }

    // Helper method to save accounts to the JSON file
    private void saveAccounts() throws IOException {
        File file = new File(ACCOUNTS_FILE_PATH);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, accounts);
    }

    // Helper method to generate the next available account ID
    private Long getNextAccountId() {
        Long maxId = 0L;
        for (Accounts account : accounts) {
            if (account.getId() > maxId) {
                maxId = account.getId();
            }
        }
        return maxId + 1;
    }

    @PostMapping("/getAccountDetails")
    public ResponseEntity<Object> getAccountDetails(@RequestBody Map<String, Object> requestBody) {
        try {
            // Load existing accounts from the JSON file
            loadAccounts();

            // Get the accountId from the request body
            Long accountId = Long.parseLong(requestBody.get("accountId").toString());

            // Find the account with the matching accountId
            Accounts requestedAccount = null;
            for (Accounts account : accounts) {
                if (account.getId().equals(accountId)) {
                    requestedAccount = account;
                    break;
                }
            }

            // If the account is found, create a response payload with the account details
            if (requestedAccount != null) {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("accountId", requestedAccount.getId());
                responseBody.put("name", requestedAccount.getName());
                responseBody.put("phone", requestedAccount.getPhone());
                responseBody.put("address", requestedAccount.getAddress());
                responseBody.put("accountType", requestedAccount.getAccountType());
                responseBody.put("balance", requestedAccount.getBalance());
                responseBody.put("username", requestedAccount.getUsername());

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving account details");
        }
    }

    //-------------------------------------------------------Deposit ---------------------------------------//

    @PostMapping("/deposit")
    public ResponseEntity<Object> deposit(@RequestBody Map<String, Object> requestBody) {
        try {
            // Load existing accounts from the JSON file
            loadAccounts();

            // Get the accountId and depositAmount from the request body
            Long accountId = Long.parseLong(requestBody.get("accountId").toString());
            Double depositAmount = Double.parseDouble(requestBody.get("depositAmount").toString());

            // Find the account with the matching accountId
            Accounts accountToDeposit = null;
            for (Accounts account : accounts) {
                if (account.getId().equals(accountId)) {
                    accountToDeposit = account;
                    break;
                }
            }

            // If the account is found, perform the deposit
            if (accountToDeposit != null) {
                // Update the account balance with the deposit amount
                Double newBalance = accountToDeposit.getBalance() + depositAmount;
                accountToDeposit.setBalance(newBalance);

                // Save the updated account list to the JSON file
                saveAccounts();

                // Create a response payload with the updated account details
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("accountId", accountToDeposit.getId());
                responseBody.put("name", accountToDeposit.getName());
                responseBody.put("phone", accountToDeposit.getPhone());
                responseBody.put("address", accountToDeposit.getAddress());
                responseBody.put("accountType", accountToDeposit.getAccountType());
                responseBody.put("balance", accountToDeposit.getBalance());
                responseBody.put("username", accountToDeposit.getUsername());

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during deposit");
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Object> withdraw(@RequestBody Map<String, Object> requestBody) {
        try {
            // Load existing accounts from the JSON file
            loadAccounts();

            // Get the accountId and withdrawalAmount from the request body
            Long accountId = Long.parseLong(requestBody.get("accountId").toString());
            Double withdrawalAmount = Double.parseDouble(requestBody.get("withdrawalAmount").toString());

            // Find the account with the matching accountId
            Accounts accountToWithdraw = null;
            for (Accounts account : accounts) {
                if (account.getId().equals(accountId)) {
                    accountToWithdraw = account;
                    break;
                }
            }

            // If the account is found, perform the withdrawal
            if (accountToWithdraw != null) {
                // Check if the withdrawal amount is valid (not exceeding the account balance)
                if (withdrawalAmount <= accountToWithdraw.getBalance()) {
                    // Update the account balance by subtracting the withdrawal amount
                    Double newBalance = accountToWithdraw.getBalance() - withdrawalAmount;
                    accountToWithdraw.setBalance(newBalance);

                    // Save the updated account list to the JSON file
                    saveAccounts();

                    // Create a response payload with the updated account details
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("accountId", accountToWithdraw.getId());
                    responseBody.put("name", accountToWithdraw.getName());
                    responseBody.put("phone", accountToWithdraw.getPhone());
                    responseBody.put("address", accountToWithdraw.getAddress());
                    responseBody.put("accountType", accountToWithdraw.getAccountType());
                    responseBody.put("balance", accountToWithdraw.getBalance());
                    responseBody.put("username", accountToWithdraw.getUsername());

                    return ResponseEntity.status(HttpStatus.OK).body(responseBody);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient balance");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during withdrawal");
        }
    }

    //------------------------------------------Delete---------------------------------------------------------------//

    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<Object> removeAccount(@PathVariable Long accountId) {
        try {
            // Load existing accounts from the JSON file
            loadAccounts();

            // Find the account with the matching accountId
            Accounts accountToRemove = null;
            for (Accounts account : accounts) {
                if (account.getId().equals(accountId)) {
                    accountToRemove = account;
                    break;
                }
            }

            // If the account is found, remove it from the accounts list
            if (accountToRemove != null) {
                accounts.remove(accountToRemove);

                // Save the updated account list to the JSON file
                saveAccounts();

                return ResponseEntity.status(HttpStatus.OK).body("Account removed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while removing account");
        }
    }


    //---------------------------------------------Modify----------------------------------------------------------//


    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<Object> updateAccount(@PathVariable Long accountId, @RequestBody Accounts updatedAccount) {
        try {
            // Load existing accounts from the JSON file
            loadAccounts();

            // Find the account with the matching ID
            Accounts accountToUpdate = null;
            for (Accounts account : accounts) {
                if (account.getId().equals(accountId)) {
                    accountToUpdate = account;
                    break;
                }
            }

            // If the account is found, update the name and account type
            if (accountToUpdate != null) {
                accountToUpdate.setName(updatedAccount.getName());
                accountToUpdate.setAccountType(updatedAccount.getAccountType());

                // Save the updated account list to the JSON file
                saveAccounts();

                return ResponseEntity.status(HttpStatus.OK).body("Account updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while updating the account");
        }
    }


}
