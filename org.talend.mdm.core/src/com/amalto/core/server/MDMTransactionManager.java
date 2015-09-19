/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.server;

import com.amalto.core.storage.transaction.Transaction;
import com.amalto.core.storage.transaction.TransactionManager;
import org.apache.log4j.Logger;

import java.util.*;

public class MDMTransactionManager implements TransactionManager {

    private static final Logger LOGGER = Logger.getLogger(MDMTransactionManager.class);

    private static final Map<Thread, Transaction> currentTransactions = new HashMap<Thread, Transaction>();

    private static final Map<String, Transaction> activeTransactions = new HashMap<String, Transaction>();

    private boolean isInitialized = false;

    @Override
    public List<String> list() {
        synchronized (activeTransactions) {
            List<String> activeTransactionIds = new ArrayList<String>(activeTransactions.size());
            activeTransactionIds.addAll(activeTransactions.keySet());
            return activeTransactionIds;
        }
    }

    @Override
    public Transaction create(Transaction.Lifetime lifetime) {
        return create(lifetime, UUID.randomUUID().toString());
    }

    @Override
    public Transaction create(Transaction.Lifetime lifetime, String transactionID) {
        if (lifetime == null) {
            throw new IllegalArgumentException("Life time argument cannot be null.");
        }
        Transaction transaction;
        synchronized (activeTransactions) {
            transaction = activeTransactions.get(transactionID);
            if (transaction == null) {
                transaction = new MDMTransaction(lifetime, transactionID);
            }
            activeTransactions.put(transaction.getId(), transaction);
        }
        synchronized (currentTransactions) {
            if (!currentTransactions.containsKey(Thread.currentThread())) {
                associate(transaction);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("New transaction: " + transaction.toString());
        }
        return transaction;
    }

    @Override
    public Transaction get(String transactionId) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction id cannot be null.");
        }
        synchronized (activeTransactions) {
            return activeTransactions.get(transactionId);
        }
    }

    @Override
    public void remove(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        if (transaction.hasFailed()) {
            LOGGER.error("Transaction " + transaction.getId() + " should not be removed from current transactions (it has failed).");
            transaction.rollback();
        }
        synchronized (currentTransactions) {
            if(currentTransactions.remove(Thread.currentThread()) != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Transaction removed: " + transaction.getId());
                }
            }
            // remove all of transactions by transaction id
            for (Iterator<Thread> it = currentTransactions.keySet().iterator(); it.hasNext();) {
                Thread thread = it.next();
                if (transaction.getId().equals(currentTransactions.get(thread).getId())) {
                    it.remove();
                    currentTransactions.remove(thread);
                }
            }
        }
        synchronized (activeTransactions) {
            activeTransactions.remove(transaction.getId());
        }
    }

    @Override
    public void init() {
        if (!isInitialized) {
            if (LOGGER.isDebugEnabled()) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                StringBuilder builder = new StringBuilder();
                                builder.append("Active transaction(s) (").append(activeTransactions.size()).append(")");
                                for (Map.Entry<String, Transaction> currentTransaction : activeTransactions.entrySet()) {
                                    builder.append(currentTransaction.getKey()).append('\n');
                                }
                                LOGGER.debug(builder.toString());
                                Thread.sleep(20000);
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                t.start();
            }
            isInitialized = true;
        }
    }

    @Override
    public void close() {
        synchronized (activeTransactions) {
            Collection<Transaction> values = new ArrayList<Transaction>(activeTransactions.values());
            for (Transaction transaction : values) {
                transaction.rollback();
            }
        }
    }

    @Override
    public Transaction currentTransaction() {
        synchronized (currentTransactions) {
            Transaction transaction = currentTransactions.get(Thread.currentThread());
            if (transaction == null) {
                return associate(create(Transaction.Lifetime.AD_HOC));
            }
            return transaction;
        }
    }

    @Override
    public Transaction associate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        synchronized (currentTransactions) {
            currentTransactions.put(Thread.currentThread(), transaction);
        }
        return transaction;
    }

    @Override
    public void dissociate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        synchronized (currentTransactions) {
            if (transaction == currentTransactions.get(Thread.currentThread())) {
                currentTransactions.remove(Thread.currentThread());
            }
        }
    }

    @Override
    public boolean hasTransaction() {
        return currentTransactions.get(Thread.currentThread()) != null;
    }
}
