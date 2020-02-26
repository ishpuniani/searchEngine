# searchEngine
Information Retrieval Assignment - Dhruv Ishpuniani, 19310617

### Steps to run:

Project path : `/home/ta/searchEngine`

1. Build the project: `mvn clean install`

2. Run project: `mvn exec:java -Dexec.mainClass="Main"`

3. Results are in src/main/resources/results/results_final/

    They contain 3 files: results.txt, trec_out.txt, pr.jpeg
- results.txt : The results used to evaluate using trec_eval
- trec_out.txt : trec_eval results
- pr.jpeg : precision recall graph for the search engine. 

4. To evaluate : 

From the Project Path

`src/main/resources/trec_eval-9.0.7/trec_eval src/main/resources/cran/QRelsCorrectedforTRECeval src/main/resources/results/results_final/results.txt`
