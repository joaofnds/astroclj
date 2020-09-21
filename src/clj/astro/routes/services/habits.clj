(ns astro.routes.services.habits
  (:require [astro.db.habits :as habits]))

(defn- serialize-habit
  [habit]
  {:id (:db/id habit)
   :name (:habit/name habit)
   :activities (map :activity/date (:habit/activities habit))})

(defn- serialize-habits
  [habits]
  (for [[habit] habits]
    (serialize-habit habit)))

(defn all-habits
  [context args value]
  (serialize-habits (habits/find-all-habits)))

(defn get-habit
  [context {:keys [name]} value]
  (if-let [habit (habits/find-habit name)]
    (serialize-habit habit)))

(defn add-habit
  [context {:keys [name]} value]
  (habits/add-habit name))

(defn add-activity
  [_ {:keys [name date]} _]
  (habits/add-activity name date))
