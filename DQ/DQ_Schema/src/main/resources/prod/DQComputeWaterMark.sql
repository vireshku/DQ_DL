CREATE TABLE IF NOT EXISTS DQComputeStore.DQComputeWaterMark
(
    SubjectArea               STRING      NOT NULL
    ,SourceEntityName         STRING      NOT NULL
    ,WaterMarkStartValue      STRING      NOT NULL
    ,WaterMarkEndValue        STRING      NOT NULL
    ,DQAppliedCreatedDate        TIMESTAMP
  ,DQAppliedModifiedDate       TIMESTAMP
   
)
USING DELTA
LOCATION 'adl://psinsightsadls01.azuredatalakestore.net/PROD/DQCompute/DQComputeStore/DQComputeWaterMark/';