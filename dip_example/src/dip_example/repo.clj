(ns dip-example.repo)

(defmulti update-entity (fn [repo-type entity] repo-type))

(defmulti load-entity (fn [repo-type entity-name] repo-type))

(defmulti whole-repo (fn [repo-type] repo-type))
