package com.ms.jedi.observer.model

case class Audit(
  Source:        String,
  Entity:        String,
  FilePath:      String,
  Region:        String,
  RefreshedTime: BigInt,
  FullDelta:  String)