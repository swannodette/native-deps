(ns leiningen.native-deps
  (:require [lancet.core :as lancet])
  (:use [leiningen.core :only [default-repos]]
        [leiningen.util.maven :only [make-dependency]]
        [leiningen.deps :only [make-repository]]
        [clojure.contrib.java-utils :only [file]])
  (:import [org.apache.maven.artifact.ant DependenciesTask]))

(defn native-deps
  [project]
  (let [deps-task (DependenciesTask.)]
    (doto deps-task
      (.setFilesetId "native-dependency.fileset")
      (.setProject lancet/ant-project)
      (.setPathId (:name project)))
    (doseq [r (map make-repository (concat default-repos
                                           (:repositories project)))]
      (.addConfiguredRemoteRepository deps-task r))
    (doseq [dep (:native-dependencies project)]
      (.addDependency deps-task (make-dependency dep)))
    (.execute deps-task)
    (lancet/unjar {:dest (:root project)} (.getReference lancet/ant-project
                                                         (.getFilesetId deps-task)))))
