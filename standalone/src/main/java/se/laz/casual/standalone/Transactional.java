package se.laz.casual.standalone;

import com.arjuna.ats.jta.transaction.Transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

public class Transactional
{
    private final TransactionManager transactionManager;

    public Transactional(TransactionManager transactionManager)
    {
        this.transactionManager = transactionManager;
    }

    public static Transactional of()
    {
        return new Transactional(com.arjuna.ats.jta.TransactionManager.transactionManager());
    }

    public Transaction startOrJoinTransaction(CasualXAResource casualXAResource)
    {
        try
        {
            Transaction transaction = (Transaction) transactionManager.getTransaction();
            if(null == transaction)
            {
                transactionManager.begin();
                transaction = (Transaction) transactionManager.getTransaction();
            }
            transaction.enlistResource(casualXAResource);
            return transaction;
        }
        catch (SystemException e)
        {
            throw new RuntimeException(e);
        }
        catch (NotSupportedException | RollbackException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void commit()
    {
        try
        {
            transactionManager.commit();
        }
        catch (HeuristicRollbackException e)
        {
            throw new RuntimeException(e);
        }
        catch (SystemException e)
        {
            throw new RuntimeException(e);
        }
        catch (HeuristicMixedException e)
        {
            throw new RuntimeException(e);
        }
        catch (RollbackException e)
        {
            throw new RuntimeException(e);
        }
    }

}
