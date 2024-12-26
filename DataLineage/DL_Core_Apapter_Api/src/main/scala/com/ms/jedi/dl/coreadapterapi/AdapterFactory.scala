/**
 * 
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.coreadapterapi

trait AdapterFactory[T] {

  lazy val instance: T = {
    val className = getClass.getCanonicalName.replaceAll("\\$$", "Impl")
    Class.forName(className).newInstance().asInstanceOf[T]
  }

}
