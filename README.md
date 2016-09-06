# cmis-client-wrapper
*cmis-client-wrapper* is a thin wrapper above Apache Chemistry. This wrapper will help the developers to intract with CMIS repositories (Alfresco) by calling its basic operations like upload/download content.

We can incorporate this wrapper in applications where we need to intract with CMIS repository for operations:
* Upload content
* Download content

### Advantages
Main benefit of this wrapper over Apache Chemistry is that it reduces the overhead of:
*  Maintaining sessions
*  Handle situations where same document is operated by multiple users at the same time. 


### Drawbacks
Althogh this wrapper provide few benefits described above, but it also narrows the features provided by Apache Chemistry.
