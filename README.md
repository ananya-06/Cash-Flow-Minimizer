# Cash-Flow-Minimizer
An effective way to minimize the number of transactions within a group!

This project aims to simplify and minimize cash flow transactions within a group. It calculates how much each person owes or is owed and minimizes the number of transactions required to settle the debts.

The user can create groups, add members, record transactions, and use an algorithm to minimize the debt transactions among members. This is similar to how apps like Splitwise work, but with a focus on algorithmic debt minimization using graphs, priority queues, and multisets.

Features
* Create Group: Create a group where you can add members.
* Add Members: Add multiple members to a group.
* Add Transactions: Record transactions where one member pays a certain amount to others in the group.
* Simplify Transactions: Use an algorithm to minimize the number of transactions among the members and calculate how much each person owes.
* Interactive UI: Easy-to-use JavaFX-based user interface to manage groups, members, and transactions.

Technologies Used
1. **JavaFX**: For building the graphical user interface (GUI).
2. **Java**: Core logic implementation and algorithm.
3. **Priority Queue**: For managing payers and payees to minimize transaction amounts.
4. **Maven**: For project dependency management.


Usage Instructions
* **Creating a Group**:
Click the "Create Group" button on the main page.
Enter the group name and create the group.

* **Adding Members**:
After creating the group, click the "Add Members" button.
Add members one by one.

* **Adding Transactions**:
Select a group and click the "Add Transaction" button.
Choose the payer and payees, enter the transaction amount, and click "Save".

* **Minimize Transactions**:
Once all transactions are added, click the "Simplify" button to calculate how much each member owes or is owed and minimize the transactions.


**Contributing**
If you want to contribute to this project, feel free to fork the repository, create a branch, and submit a pull request.
