package com.ms.jedi.observer.exec

object LakeObserverExecutor extends LakeObserverExecutorTrait {

  def main(args: Array[String]): Unit = {
    doObserveAndWrite(args.apply(0))
  }
}