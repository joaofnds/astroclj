(ns astro.routes.services.habits
  (:require [astro.db.habits :as habits]))

(def habits (atom {}))

(defn all-habits
  [context args value]
  (habits/all-habits))

(defn get-habit
  [context {:keys [name]} value]
  (first (filter #(= name (:name %)) @habits)))

(defn add-habit
  [context {:keys [name]} value]
  (habits/add-habit name))

(defn add-activity
  [_ {:keys [name date]} _]
  (habits/register-activity name date))
