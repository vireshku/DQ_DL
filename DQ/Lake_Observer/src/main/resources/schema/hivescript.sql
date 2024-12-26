CREATE TABLE IF NOT EXISTS ComputeStore.LakeObserverPPE
(
    Source               		STRING      NOT NULL,
	Entity               		STRING      NOT NULL,
	FilePath               		STRING      NOT NULL,
	Region						STRING      NOT NULL,
	RefreshedTime				BIGINT      NOT NULL,
	FullDelta  					STRING     	NOT NULL
  
)
USING DELTA
PARTITIONED BY (Region)
LOCATION 'adl://psinsightsadlsdev01.azuredatalakestore.net/PPE/Observer/'

----------------------------------

%sql
CREATE TABLE IF NOT EXISTS ComputeStore.LakeObserverPROD
(
    Source               		STRING      NOT NULL,
	Entity               		STRING      NOT NULL,
	FilePath               		STRING      NOT NULL,
	Region						STRING      NOT NULL,
	RefreshedTime				BIGINT      NOT NULL,
	FullDelta  					STRING     	NOT NULL
  
)
USING DELTA
PARTITIONED BY (Region)
LOCATION 'adl://psinsightsadls01.azuredatalakestore.net/PROD/Observer/'