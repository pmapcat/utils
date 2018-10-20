;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leachim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-13-10 20:27 <mklimoff222@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.fs
  (:require [clojure.java.io :as io]
            [thereisnodot.akronim.core :refer [defns]])
  (:import  [java.util.zip ZipOutputStream ZipEntry]))

(defn temp-file
  ([]
   (temp-file "cljtmpfile" ""))
  ([prefix extension]
   (java.io.File/createTempFile prefix extension)))

(defn zip-dir
  "will zip directory into a folder"
  [archive-name directory]
  (with-open [zip (ZipOutputStream. (io/output-stream archive-name))]
    (doseq [f (file-seq (io/file directory)) :when (.isFile f)]
      (.putNextEntry zip (ZipEntry. (.getPath f)))
      (io/copy f zip)
      (.closeEntry zip))))
