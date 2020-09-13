(ns astro.routes.services.graphql
  (:require
   [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
   [com.walmartlabs.lacinia.schema :as schema]
   [com.walmartlabs.lacinia :as lacinia]
   [clojure.data.json :as json]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [ring.util.http-response :refer :all]
   [mount.core :refer [defstate]]
   [astro.routes.services.habits :as habits]
   [cheshire.core :refer [parse-string]]))

(def resolvers {:all-habits   habits/all-habits
                :get-habit    habits/get-habit
                :add-habit    habits/add-habit
                :add-activity habits/add-activity})

(defstate compiled-schema
  :start
  (-> "graphql/schema.edn"
      io/resource
      slurp
      edn/read-string
      (attach-resolvers resolvers)
      schema/compile))

(defn execute-request [query]
  (let [params (parse-string query true)
        query (:query params)
        vars (:variables params)
        context nil]
    (-> (lacinia/execute compiled-schema query vars context)
        (json/write-str))))
