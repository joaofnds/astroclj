(ns astro.db.habits
  (:require [astro.db.core :refer [conn]]
            [datomic.api :as d]))

(defn find-all-habits
  []
  (d/q '[:find (pull ?h [:db/id :habit/name {:habit/activities [:activity/date]}])
         :where [?h :habit/name]]
       (d/db conn)))

(defn find-habit
  [name]
  (ffirst (d/q '[:find (pull ?h [:db/id :habit/name {:habit/activities [:activity/date]}])
                 :in $ ?name
                 :where [?h :habit/name ?name]]
               (d/db conn)
               name)))

(defn add-habit
  [habit-name]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :habit/name habit-name}]))

(defn add-activity
  [habit-name activity-date]
  (let [activity-id (d/tempid :db.part/user)
        habit-id (:db/id (find-habit habit-name))]
    @(d/transact conn [{:db/id activity-id
                        :activity/date activity-date}
                       {:db/id habit-id
                        :habit/activities activity-id}])))
