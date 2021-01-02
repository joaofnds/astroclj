(ns astro.events.docs
  (:require
   [re-frame.core :as rf]
   [ajax.core :as ajax]))

(rf/reg-event-db
 :set-docs
 (fn [db [_ docs]]
   (assoc db :docs docs)))

(rf/reg-sub
 :docs
 (fn [db _]
   (:docs db)))

(rf/reg-event-fx
 :fetch-docs
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "/docs"
                 :response-format (ajax/raw-response-format)
                 :on-success      [:set-docs]}}))
