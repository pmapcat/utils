(defproject thereisnodot/utils "0.1.0-SNAPSHOT"
  :description "Utils: Spreadsheet"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [dk.ative/docjure "1.12.0"]
                 [org.unbescape/unbescape "1.0"]
                 [ring/ring-codec "1.1.1"]
                 [com.atlassian.commonmark/commonmark "0.11.0"]
                 [com.atlassian.commonmark/commonmark-ext-gfm-tables "0.11.0"]
                 [com.atlassian.commonmark/commonmark-ext-autolink "0.11.0"]
                 [com.atlassian.commonmark/commonmark-ext-gfm-strikethrough "0.11.0"]
                 [com.atlassian.commonmark/commonmark-ext-heading-anchor "0.11.0"]
                 [com.atlassian.commonmark/commonmark-ext-yaml-front-matter "0.11.0"]
                 [com.atlassian.commonmark/commonmark-ext-ins "0.11.0" ]
                 [org.jsoup/jsoup "1.8.3"]]
  :plugins [[cider/cider-nrepl "0.17.0"]]
  :java-source-paths ["src/java"])
