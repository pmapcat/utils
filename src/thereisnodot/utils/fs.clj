;; @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
;; @ Copyright (c) Michael Leahcim                                                      @
;; @ You can find additional information regarding licensing of this work in LICENSE.md @
;; @ You must not remove this notice, or any other, from this software.                 @
;; @ All rights reserved.                                                               @
;; @@@@@@ At 2018-10-20 21:49 <thereisnodotcollective@gmail.com> @@@@@@@@@@@@@@@@@@@@@@@@

(ns thereisnodot.utils.fs
  (:require [clojure.java.io :as io]
            [thereisnodot.akronim.core :refer [defns]])
  (:import  [java.util.zip ZipOutputStream ZipEntry]))

(defns temp-file
  "Will return temp file handler without actually making it"
  {:no-test? true}
  [(str (temp-file)) => "/tmp/cljtmpfile1555885885588503551"]
  ([]
   (temp-file "cljtmpfile" ""))
  ([prefix extension]
   (java.io.File/createTempFile prefix extension)))

(defns zip-dir
  "will zip directory into a folder"
  {:no-test? true}
  [(zip-dir
    "/home/mik/Downloads/alacritty.zip"
    "/home/mik/Downloads/alacritty") => nil]
  [archive-name directory]
  (with-open [zip (ZipOutputStream. (io/output-stream archive-name))]
    (doseq [f (file-seq (io/file directory)) :when (.isFile f)]
      (.putNextEntry zip (ZipEntry. (.getPath f)))
      (io/copy f zip)
      (.closeEntry zip))))

