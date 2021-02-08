/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcar;

import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;


@Contract(
        name = "Fabcar",
        info = @Info(
                title = "TradeItem contract",
                description = "The hyperlegendary Item contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")))
@Default
public final class Fabcar implements ContractInterface {

    private final Genson genson = new Genson();
    private enum ItemErrors {
        ITEM_NOT_FOUND,
        ITEM_ALREADY_EXISTS
    }

    private enum TokenErrors {
        HAVING_LESS_TOKEN
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        stub.putStringState("admin", "1000000");
        stub.putStringState("MaximumHistory", "0");
        stub.putStringState("MaximumItem", "0");
    }


    @Transaction()
    public Item registerItem(final Context ctx, final String name, final String owner) {
        ChaincodeStub stub = ctx.getStub();
        String max = stub.getStringState("MaximumItem");
        String key = "ITEM" + max;
        String stateOfKey = stub.getStringState(key);
        int m = Integer.parseInt(max);
        m++;
        Item item = new Item(name, owner, 0);
        stub.putStringState("MaximumItem", Integer.toString(m));
        stateOfKey = genson.serialize(item);
        stub.putStringState("ITEM" + Integer.toString(m), stateOfKey);
        return item;
    }

    @Transaction()
    public String transfer(final Context ctx, final String from, final String to, final int amount) {
        ChaincodeStub stub = ctx.getStub();
        String sender = stub.getStringState(from);
        String receiver = stub.getStringState(to);
        if (sender.isEmpty()) {
            String errorMessage = String.format("%s has not balances", from);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, TokenErrors.HAVING_LESS_TOKEN.toString());
        }
        if (receiver.isEmpty()) {
            receiver = "0";
        }
        int senderBalances = Integer.parseInt(sender);
        int rec = Integer.parseInt(receiver);
        if (senderBalances < amount) {
            String errorMessage = String.format("%s has not balances", to);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, TokenErrors.HAVING_LESS_TOKEN.toString());
        }

        senderBalances = senderBalances - amount;
        rec = rec + amount;
        stub.putStringState(from, Integer.toString(senderBalances));
        stub.putStringState(to, Integer.toString(rec));
        return "success";
    }

    @Transaction()
    public Item sellMyItem(final Context ctx, final String key, final int price) {
        ChaincodeStub stub = ctx.getStub();
        String stateOfKey = stub.getStringState(key);
        if (stateOfKey.isEmpty()) {
            String errorMessage = String.format("Item %s is not exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ItemErrors.ITEM_NOT_FOUND.toString());
        }
        Item item = genson.deserialize(stateOfKey, Item.class);
        item.setMarketState(true);
        item.setPrice(price);
        stateOfKey = genson.serialize(item);
        stub.putStringState(key, stateOfKey);
        return item;
    }

    @Transaction()
    public Item buyUserItem(final Context ctx, final String key, final String buyer)  {
        ChaincodeStub stub = ctx.getStub();
        String stateOfKey = stub.getStringState(key);
        String max = stub.getStringState("MaximumHistory");
        int m = Integer.parseInt(max);
        if (stateOfKey.isEmpty()) {
            String errorMessage = String.format("Item %s is not exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ItemErrors.ITEM_NOT_FOUND.toString());
        }
        Item item = genson.deserialize(stateOfKey, Item.class);
        transfer(ctx, buyer, item.getOwner(), item.getPrice());
        ArrayList<String> hist = new ArrayList<String>();
        hist.add(key);
        hist.add(item.getName());
        hist.add(buyer);
        hist.add(item.getPrice().toString());
        changeItemOwner(ctx, key, buyer);
        m++;
        String histSerialization = genson.serialize(hist);
        stub.putStringState("HISTORY" + Integer.toString(m), histSerialization);
        stub.putStringState("MaximumHistory", Integer.toString(m));

        return item;
    }

    @Transaction()
    public Item changeItemOwner(final Context ctx, final String key, final String newowner)  {
        ChaincodeStub stub = ctx.getStub();
        String stateOfKey = stub.getStringState(key);
        if (stateOfKey.isEmpty()) {
            String errorMessage = String.format("Item %s is not exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ItemErrors.ITEM_ALREADY_EXISTS.toString());
        }
        Item item = genson.deserialize(stateOfKey, Item.class);
        Item newItem = new Item(item.getName(), newowner, 0);
        stateOfKey = genson.serialize(newItem);
        stub.putStringState(key, stateOfKey);
        return item;
    }

    @Transaction()
    public String getMyItems(final Context ctx, final String owner)  {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "ITEM1";
        final String endKey = "ITEM99";
        List<ItemQueryResult> queryResults = new ArrayList<ItemQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Item item = genson.deserialize(result.getStringValue(), Item.class);
            if (item.getOwner().equals(owner)) {
                queryResults.add(new ItemQueryResult(result.getKey(), item));
            }
        }

        final String response = genson.serialize(queryResults);

        return response;
    }


    @Transaction()
    public String getAllRegisteredItems(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "ITEM1";
        final String endKey = "ITEM99";
        List<ItemQueryResult> queryResults = new ArrayList<ItemQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Item item = genson.deserialize(result.getStringValue(), Item.class);
            queryResults.add(new ItemQueryResult(result.getKey(), item));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    @Transaction()
    public String getAllOrderedItems(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "ITEM1";
        final String endKey = "ITEM99";
        List<ItemQueryResult> queryResults = new ArrayList<ItemQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Item item = genson.deserialize(result.getStringValue(), Item.class);
            if (item.getMarketState()) {
                queryResults.add(new ItemQueryResult(result.getKey(), item));
            }
        }
        final String response = genson.serialize(queryResults);

        final String startHistKey = "HISTORY1";
        final String endHistKey = "HISTORY99";
        List<String> queryHistResults = new ArrayList<String>();
        QueryResultsIterator<KeyValue> resultOfHist = stub.getStateByRange(startHistKey, endHistKey);
        for (KeyValue result: resultOfHist) {
            queryHistResults.add(result.getStringValue());
        }
        final String responseHist = genson.serialize(queryHistResults);
        return response + "&" + responseHist;
    }

    @Transaction()
    public int getBalance(final Context ctx, final String name) {
        ChaincodeStub stub = ctx.getStub();
        String stateOfName = stub.getStringState(name);
        if (stateOfName.isEmpty()) {
            String errorMessage = String.format("%s has not balances", name);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, TokenErrors.HAVING_LESS_TOKEN.toString());
        }
        int m = Integer.parseInt(stateOfName);
        return m;
    }
}

