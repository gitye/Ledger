package ledger.database.entity;

/**
 * Entity that holds a single Account
 */
public class Account implements IEntity, Comparable<Account> {
    private String name;
    private String description;
    private int id;

    public Account(String name, String description) {
        this(name, description, -1);
    }

    public Account(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    /**
     * Gets the Account name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Account name
     *
     * @param name new name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Account description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the Account description
     *
     * @param description new account description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Account ID
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the Account ID
     *
     * @param id new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Determines equality between this Account and another object
     *
     * @param o The object to compare to this Account
     * @return True if this Account is equal to the provided object. False otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account)) return false;

        Account acc = (Account) o;

        if (!this.getName().equals(acc.getName())) return false;
        return this.getDescription().equals(acc.getDescription());
    }

    @Override
    public String toString() {
        return String.format("%s: %s", this.name, this.description);
    }

    @Override
    public int compareTo(Account o) {
        return ((Integer) this.getId()).compareTo(o.getId());
    }
}
