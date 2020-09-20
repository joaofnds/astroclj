(ns astro.db.habits
  (:require [astro.db.core :refer [conn]]
            [datomic.api :as d]))

(defn- find-all-habits
  []
  (d/q '[:find (pull ?h [:db/id :habit/name {:habit/activities [:activity/date]}])
         :where [?h :habit/name]]
       (d/db conn)))

(defn adapt-find-all-habits
  [habits]
  (for [[habit] habits]
    {:id (:db/id habit)
     :name (:habit/name habit)
     :activities (map :activity/date (:habit/activities habit))}))

(defn all-habits []
  (adapt-find-all-habits (find-all-habits)))

(defn find-habit-activities
  [habit-name]
  (d/q '[:find ?activity-date
         :in $ ?habit-name
         :where
         [?eid :habit/name ?habit-name]
         [?eid :habit/activities ?activity]
         [?activity :activity/date ?activity-date]]
       (d/db conn)
       habit-name))

(defn find-habit-id
  [habit-name]
  (ffirst (d/q '[:find ?eid
                 :in $ ?habit-name
                 :where [?eid :habit/name ?habit-name]]
               (d/db conn)
               habit-name)))

(defn add-habit
  [habit-name]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :habit/name habit-name}]))

(defn register-activity
  [habit-name activity-date]
  (let [activity-id (d/tempid :db.part/user)
        habit-id (find-habit-id habit-name)]
    @(d/transact conn [{:db/id activity-id
                        :activity/date activity-date}
                       {:db/id habit-id
                        :habit/activities activity-id}])))
