CREATE TABLE IF NOT EXISTS DQComputeStore.DQFileAudit
(
    timeGenerated               STRING      NOT NULL
    ,filePath                   STRING      NOT NULL
    ,entityName                 STRING      NOT NULL
    ,timeGeneratedInNum         BIGINT     NOT NULL
    ,isFileFullProcessed        BOOLEAN     NOT NULL
    ,filePostIndicator          BOOLEAN
    ,EnvironmentType            STRING      NOT NULL
    ,DataStoreType              STRING      NOT NULL
    ,SubjectAreaName            STRING      NOT NULL
)
USING DELTA
LOCATION 'adl://psinsightsadlsdev01.azuredatalakestore.net/PPE/Audit'