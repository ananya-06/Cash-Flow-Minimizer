package com.ananya.ctrl;

import com.ananya.algoUtil.AlgorithmUtil;
import com.ananya.vo.Transaction;
import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    private ObservableList<String> groupNames ;
    private Map<String, List<String>> groupMap ;
    private Map<String, List<Transaction>> transactionMap;
    private ObservableList<String> memberNames;
    private ObservableList<String> payees;

    @FXML
    public VBox addMemberPane;
    @FXML
    public VBox addTransactionPane;
    @FXML public VBox addMinimizingPane;

    @FXML
    public ComboBox<String> groupDropdown;
    @FXML
    public TextField memberNameField;
    @FXML
    public Label memberStatusLabel;

    @FXML
    public VBox transactionPane;
    @FXML public ScrollPane transactionScrollPane;
    @FXML
    public VBox groupListContainer;
    @FXML
    public Label emptyMessage;

    @FXML public ComboBox<String> groupDropdownForMinimizing;

    @FXML
    public ComboBox<String> groupDropdownForTransaction;
    @FXML
    public ComboBox<String> payerDropdown;
    @FXML
    public ListView<String> payeeList;
    @FXML
    public TextField amountField;
    @FXML public TextField descriptionField;
    @FXML public Label transactionStatusLabel;

    @FXML
    public void initialize() {
        /* initializing our data structures */
        groupNames = FXCollections.observableArrayList();
        groupMap = new HashMap<>();
        transactionMap = new HashMap<>();
        memberNames = FXCollections.observableArrayList();
        payees = FXCollections.observableArrayList();

        /* Initially, hide all functional panes */
        hideAllPanes();

        /* Other setting required*/
        /* Setting selection mode as Multiple for Payees*/
        payeeList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        /* Need to bind the observable list to my grp and transactions comboBox*/
        groupDropdown.setItems(groupNames);
        groupDropdownForTransaction.setItems(groupNames);
        groupDropdownForTransaction.setOnAction(event -> handleMemberDropdown(String.valueOf(groupDropdownForTransaction.getValue())));
        payerDropdown.setItems(memberNames);
        payerDropdown.setOnAction(event -> handlePayeeListDisplay(payerDropdown.getValue()));
        payeeList.setItems(payees);
        groupDropdownForMinimizing.setItems(groupNames);
    }

    /**
     * Currently there are two panes whose visibility is being managed:
     *  1. member pane
     *  2. transaction pane
     * Initially, both need to be hidden. This method manages that.
     */
    private void hideAllPanes() {
        addMemberPane.setVisible(false);
        addMemberPane.setManaged(false);
        AnchorPane.clearConstraints(addMemberPane);

        addTransactionPane.setVisible(false);
        addTransactionPane.setManaged(false);
        AnchorPane.clearConstraints(addTransactionPane);

        addMinimizingPane.setVisible(false);
        addMinimizingPane.setManaged(false);
        AnchorPane.clearConstraints(addMinimizingPane);
    }

    @FXML
    public void showAddMemberPane() {
        hideAllPanes();
        addMemberPane.setVisible(true);
        addMemberPane.setManaged(true);
    }

    @FXML
    public void showAddTransactionPane() {
        hideAllPanes();
        addTransactionPane.setVisible(true);
        addTransactionPane.setManaged(true);
    }

    @FXML public void showAddMinimizePane() {
        hideAllPanes();
        addMinimizingPane.setVisible(true);
        addMinimizingPane.setManaged(true);
    }

    public void createGroup() {
        hideAllPanes();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Group");
        dialog.setHeaderText("Enter the name of the group:");
        dialog.setContentText("Group Name:");

        dialog.showAndWait().ifPresent(groupName -> {
            if (!groupName.isEmpty()) {
                /*removing the empty message*/
                if(groupNames.isEmpty()){
                    groupListContainer.getChildren().remove(emptyMessage);
                }

                if(groupNames.contains(groupName)){
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setContentText("This group is already existing. Enter a new one.");
                    error.setTitle("Error");
                    error.setHeaderText("Invalid group name");
                    error.showAndWait();
                    return;
                }

                /* adding to the group list*/
                groupNames.add(groupName);
                groupMap.put(groupName, new ArrayList<>());

                TitledPane titledPane = createGroupPane(groupName);
                groupListContainer.getChildren().add(titledPane);

                /*displaying content in the right transaction pane*/
                Label label = new Label("Group Created: " + groupName);
                transactionPane.getChildren().add(label);
            }
            else {
                Alert notEmpty = new Alert(Alert.AlertType.ERROR);
                notEmpty.setHeaderText("Invalid Group Name");
                notEmpty.setTitle("Error");
                notEmpty.setContentText("The group name cannot be empty!");
                notEmpty.showAndWait();
            }

        });
    }

    private void updateGroupMembers(@NotNull String groupName) {
        // Find the TitledPane for the group
        for (javafx.scene.Node node : groupListContainer.getChildren()) {
            if (node instanceof TitledPane && ((TitledPane) node).getText().equals(groupName)) {
                TitledPane titleNode = (TitledPane) node;
                // Get the ListView from the content of the TitledPane
                if (titleNode.getContent() instanceof ListView<?>) {
                    ListView<String> memberListView = (ListView<String>) titleNode.getContent();

                    List<String> members = groupMap.get(groupName);
                    if (members != null && !members.isEmpty()) {
                        memberListView.getItems().setAll(members);
                    } else {
                        memberListView.getItems().setAll("No data");
                    }
                }
            }
        }
    }

    private TitledPane createGroupPane(@NotNull String groupName) {
        /* Create a ListView to display group members */
        ListView<String> memberListView = new ListView<>();
        updateMemberList(groupName, memberListView);

        TitledPane titledPane = new TitledPane(groupName, memberListView);
        titledPane.setText(groupName);
        titledPane.setExpanded(false); // Initially collapsed

        return titledPane;
    }

    private void updateMemberList(String groupName, ListView<String> memberListView) {
        List<String> members = groupMap.get(groupName);
        if(members!=null && !members.isEmpty()){
            memberListView.getItems().setAll(members);
        }
        else memberListView.getItems().setAll("No data");
    }

    public void simplify() {
        // just for logging
        System.out.println("All transactions added :: " + transactionMap.toString());

        AlgorithmUtil.memberNetBalance = new HashMap<>();
        String groupName = groupDropdownForMinimizing.getValue();

        /* if there are no transactions for the group, then need to show alert */
        if(transactionMap.get(groupName).isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Could not proceed");
            alert.setHeaderText("No transactions added for this group!");
            alert.setContentText("Since no transactions are added to the selected group, the request could not be processed.");
            alert.showAndWait();
            hideAllPanes();
            return;
        }

        /* sending corresponding transactions list for finding out the Net Balances */
        AlgorithmUtil.calculateNetBalances(transactionMap.get(groupName));
        AlgorithmUtil.createGettersGiversList();
        List<String> minimizedTransactions = AlgorithmUtil.getMinimizedTransactions();
        System.out.println("Minimized transactions :: " + minimizedTransactions.toString());

        for(String minimizedTsn : minimizedTransactions){
            Label label = new Label(minimizedTsn);
            label.setStyle("-fx-text-fill: green;");
            transactionPane.getChildren().add(label);
        }
        Label label = new Label("Total number of transactions reduced to " + AlgorithmUtil.minTsnCounter + "!!");
        label.setStyle("-fx-text-fill: green;");
        transactionPane.getChildren().add(label);
    }

    public void handleAddMember() {
        String groupName = groupDropdown.getValue();
        String memberName = memberNameField.getText().trim();

        if (groupName == null) {
            memberStatusLabel.setText("Please select a group!");
            return;
        }

        /* add to my member list */
        if (!memberName.isEmpty()) {
            /* add the member to the selected group*/
            groupMap.putIfAbsent(groupName, new ArrayList<>());
            List<String> members = groupMap.get(groupName);
            if (members.contains(memberName)) {
                memberStatusLabel.setText("Member already exists!");
                return;
            }
            members.add(memberName);
            memberStatusLabel.setText(memberName + " added to group " + groupName + ".");

            /* also need to update the UI accordingly*/
            updateGroupMembers(groupName);

            /* transaction pane logic*/
            Label label = new Label("Member Added: " + memberName);
            transactionPane.getChildren().add(label);
        }
        else {
            memberStatusLabel.setText("Member name cannot be empty!");
        }
        memberNameField.setText("");
    }

    public void handleAddTransaction() {
        String groupName = groupDropdownForTransaction.getValue();
        if(groupMap.get(groupName).size()<3){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No action possible");
            alert.setHeaderText("Less than 3 members present in the selected group!");
            alert.setContentText("Before adding a transaction, kindly ensure there are atleast 3 members added to the group.");
            alert.showAndWait();
            return;
        }
        String payer = payerDropdown.getValue();
        List<String> payees = new ArrayList<>(payeeList.getSelectionModel().getSelectedItems());
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        if (groupName == null || payer == null || payees.isEmpty() || amountText.isEmpty()) {
            transactionStatusLabel.setText("All fields are required!");
            return;
        }

        //if (!payees.contains(payer)) {
            try {
                double amount = Double.parseDouble(amountText);
                transactionMap.putIfAbsent(groupName, new ArrayList<>());
                Transaction tsn = new Transaction(payer, payees, amount, description);
                transactionMap.get(groupName).add(tsn);
                System.out.println("transactionMap: " + transactionMap.toString());
                transactionStatusLabel.setText("Transaction added!");
                System.out.println("transaction added :: " + tsn);

                /* transaction pane logic*/
                Label label = new Label("New transaction Added in Group " + groupName + ": " + tsn);
                label.setWrapText(true); // Enable word wrap
                label.setMaxWidth(500.0);
                label.setStyle("-fx-text-fill: blue;"); // Set text color to green
                transactionPane.getChildren().add(label);
            } catch (NumberFormatException e) {
                transactionStatusLabel.setText("Invalid amount!");
            }
//        } else {
//            transactionStatusLabel.setText("Payer cannot be a payee!");
//        }

        /* clearing previous transaction values */
        amountField.clear();
        descriptionField.clear();
    }

    private void handleMemberDropdown(String groupName) {
        System.out.println("Inside handleMemberDropdown :: groupName=" + groupName);
        if(groupName==null){
            memberNames.clear();
        }
        else{
            memberNames.setAll(groupMap.getOrDefault(groupName, new ArrayList<>()));
        }
    }

    private void handlePayeeListDisplay(String payer) {
        payees.setAll(memberNames);
        //payees.remove(payer);
    }

}