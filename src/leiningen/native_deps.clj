(ns leiningen.native-deps
  (:require [lancet])
  (:use [leiningen.pom :only [default-repos]]
        [leiningen.deps :only [make-repository make-dependency]]
        [clojure.contrib.java-utils :only [file]])
  (:import [org.apache.maven.artifact.ant DependenciesTask]))

(defn native-deps
  [project]
  (let [deps-task (DependenciesTask.)]
    (.setFilesetId deps-task "native-dependency.fileset")
    (.setProject deps-task lancet/ant-project)
    (.setPathId deps-task (:name project))
    (doseq [r (map make-repository (concat default-repos
                                           (:repositories project)))]
      (.addConfiguredRemoteRepository deps-task r))
    (doseq [dep (:native-dependencies project)]
      (.addDependency deps-task (make-dependency dep)))
    (.execute deps-task)
    (lancet/unjar {:dest ""} (.getReference lancet/ant-project
                                            (.getFilesetId deps-task)))))
