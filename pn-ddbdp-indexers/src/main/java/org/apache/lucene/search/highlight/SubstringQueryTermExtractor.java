package org.apache.lucene.search.highlight;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;

/**
 * 
 * @author B. Armintor, after org.apache.lucene.search.highlight.QueryTermExtractor
 *
 */
public class SubstringQueryTermExtractor {

    /**
     * Extracts all terms texts of a given Query into an array of WeightedTerms
     *
     * @param query      Query to extract term texts from
     * @return an array of the terms used in a query, plus their weights.
     */
    public static final WeightedTerm[] getTerms(Query query) 
    {
        return getTerms(query,false);
    }

    /**
     * Extracts all terms texts of a given Query into an array of WeightedTerms
     *
     * @param query      Query to extract term texts from
     * @param reader used to compute IDF which can be used to a) score selected fragments better 
     * b) use graded highlights eg chaning intensity of font color
     * @param fieldName the field on which Inverse Document Frequency (IDF) calculations are based
     * @return an array of the terms used in a query, plus their weights.
     */
    public static final WeightedTerm[] getIdfWeightedTerms(Query query, IndexReader reader, String fieldName) 
    {
        WeightedTerm[] terms=getTerms(query,false, fieldName);
        int totalNumDocs=reader.numDocs();
        for (int i = 0; i < terms.length; i++)
        {
            try
            {
                int docFreq=reader.docFreq(new Term(fieldName,terms[i].term));
                //IDF algorithm taken from DefaultSimilarity class
                float idf=(float)(Math.log((float)totalNumDocs/(double)(docFreq+1)) + 1.0);
                terms[i].weight*=idf;
            } 
            catch (IOException e)
            {
                //ignore 
            }
        }
        return terms;
    }

    /**
     * Extracts all terms texts of a given Query into an array of WeightedTerms
     *
     * @param query      Query to extract term texts from
     * @param prohibited <code>true</code> to extract "prohibited" terms, too
     * @param fieldName  The fieldName used to filter query terms
   * @return an array of the terms used in a query, plus their weights.
   */
    public static final WeightedTerm[] getTerms(Query query, boolean prohibited, String fieldName) 
    {
        HashSet terms=new HashSet();
        if(fieldName!=null)
        {
            fieldName=fieldName.intern();
        }
        getTerms(query,terms,prohibited,fieldName);
        return (WeightedTerm[]) terms.toArray(new WeightedTerm[0]);
    }
    
    /**
     * Extracts all terms texts of a given Query into an array of WeightedTerms
     *
     * @param query      Query to extract term texts from
     * @param prohibited <code>true</code> to extract "prohibited" terms, too
   * @return an array of the terms used in a query, plus their weights.
   */
    public static final WeightedTerm[] getTerms(Query query, boolean prohibited) 
    {
        return getTerms(query,prohibited,null);
    }   

    //fieldname MUST be interned prior to this call
    private static final void getTerms(Query query, HashSet terms,boolean prohibited, String fieldName) 
    {
        try
        {
            if (query instanceof BooleanQuery)
                getTermsFromBooleanQuery((BooleanQuery) query, terms, prohibited, fieldName);
            else
                if(query instanceof FilteredQuery)
                    getTermsFromFilteredQuery((FilteredQuery)query, terms,prohibited, fieldName);
                else
            {
                HashSet nonWeightedTerms=new HashSet();
                query.extractTerms(nonWeightedTerms);
                for (Iterator iter = nonWeightedTerms.iterator(); iter.hasNext();)
                {
                    Term term = (Term) iter.next();
                    if((fieldName==null)||(term.field()==fieldName))
                    {
                        terms.add(new WeightedTerm(query.getBoost(),term.text()));
                    }
                }
            }
          }
          catch(UnsupportedOperationException ignore)
          {
              //this is non-fatal for our purposes
          }                                     
    }

    /**
     * extractTerms is currently the only query-independent means of introspecting queries but it only reveals
     * a list of terms for that query - not the boosts each individual term in that query may or may not have.
     * "Container" queries such as BooleanQuery should be unwrapped to get at the boost info held
     * in each child element. 
     * Some discussion around this topic here:
     * http://www.gossamer-threads.com/lists/lucene/java-dev/34208?search_string=introspection;#34208
     * Unfortunately there seemed to be limited interest in requiring all Query objects to implement
     * something common which would allow access to child queries so what follows here are query-specific
     * implementations for accessing embedded query elements. 
     */
    private static final void getTermsFromBooleanQuery(BooleanQuery query, HashSet terms, boolean prohibited, String fieldName)
    {
        BooleanClause[] queryClauses = query.getClauses();
        for (int i = 0; i < queryClauses.length; i++)
        {
            if (prohibited || queryClauses[i].getOccur()!=BooleanClause.Occur.MUST_NOT)
                getTerms(queryClauses[i].getQuery(), terms, prohibited, fieldName);
        }
    }   
    private static void getTermsFromFilteredQuery(FilteredQuery query, HashSet terms, boolean prohibited, String fieldName)
    {
        getTerms(query.getQuery(),terms,prohibited,fieldName);      
    }
    
}

