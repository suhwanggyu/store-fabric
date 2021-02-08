/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcar;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Item {

    @Property()
    private final String name;

    @Property()
    private final String owner;
    @Property()
    private boolean marketState;
    @Property()
    private Integer price;

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public boolean getMarketState() {
        return marketState;
    }

    public Integer getPrice() {
        return price;
    }

    public void setMarketState(final boolean state) {
        this.marketState = state;
    }

    public void setPrice(final int amount) {
        this.price = amount;
    }

    public Item(@JsonProperty("name") final String name, @JsonProperty("owner") final String owner,
 @JsonProperty("price") final Integer price) {
        this.name = name;
        this.owner = owner;
        this.price = price;
        this.marketState = false;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Item other = (Item) obj;

        return Objects.deepEquals(new String[] {getName(), getOwner()},
                new String[] {other.getName(), other.getOwner()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getOwner());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [make="
+ ", owner=" + owner + "]";
    }
}

