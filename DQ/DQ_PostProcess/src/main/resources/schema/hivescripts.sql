
----------------------------------------------

%sql
CREATE TABLE IF NOT EXISTS ComputeStore.DQMetricOverAllNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


------------------------------------------


%sql
CREATE TABLE IF NOT EXISTS ComputeStore.DQMetricOverAllPerWeekNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 



------------------------------------------

%sql
CREATE TABLE IF NOT EXISTS ComputeStore.DQMetricOverAllPerDayNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 
-----------------------------------------


CREATE TABLE IF NOT EXISTS ComputeStore.DQMetricOverAllPerMonthNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


-------------------------------------------

%sql
CREATE TABLE IF NOT EXISTS ComputeStore.DQMetricOverAllPerYearNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 



-------------------------------------------


%sql
CREATE TABLE IF NOT EXISTS ComputeStore.DQMetricOverAllPerQuarterNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 



-------------------------------------------


%sql
CREATE TABLE IF NOT EXISTS ComputeStore.UniqueDimensionMetricNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


-----------------------------------------


%sql
CREATE TABLE IF NOT EXISTS ComputeStore.AccuracyDimensionMetricNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)     NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


---------------------------------------------


%sql
CREATE TABLE IF NOT EXISTS ComputeStore.CompletenessDimensionMetricNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage           DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


---------------------------------------------

%sql
CREATE TABLE IF NOT EXISTS ComputeStore.ValidDimensionMetricNeo
(
    Source                    STRING      NOT NULL,
    EntityName                STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)     NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


--------------------------------------------

%sql
CREATE TABLE IF NOT EXISTS ComputeStore.DQPerSourceMetricsNeo
(
    Source                    STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


------------------------------------


%sql
CREATE TABLE IF NOT EXISTS ComputeStore.DQPerSubAreaMetricsNeo
(
    SubArea                    STRING      NOT NULL,
    PassPercentage            DECIMAL(3,2)      NOT NULL,
    FailedPercentage          DECIMAL(3,2)      NOT NULL,
    DQAppliedCreatedDate        TIMESTAMP,
    DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA 


-------------------------------
