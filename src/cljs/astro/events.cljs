(ns astro.events
  (:require
   [re-frame.core :as rf]
   [astro.events.routing]
   [astro.events.docs]
   [astro.events.form]
   [astro.events.habits]))

(rf/reg-event-db
 :common/set-error
 (fn [db [_ error]]
   (assoc db :common/error error)))

(rf/reg-sub
 :common/error
 (fn [db _]
   (:common/error db)))

(rf/reg-event-fx
 :page/init-home
 (fn [_ _]
   {:dispatch [:fetch-docs]}))

(rf/dispatch [:fetch-habits])
