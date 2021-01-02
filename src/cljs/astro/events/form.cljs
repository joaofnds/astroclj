(ns astro.events.form
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :form.name
 (fn [db]
   (get-in db [:form :name])))

(rf/reg-event-db
 :form.name.changed
 (fn [db [_ name]]
   (assoc-in db [:form :name] name)))
