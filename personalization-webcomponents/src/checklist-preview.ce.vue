<template>
  <div>
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="mucIconsSprite" />
    <!-- eslint-disable-next-line vue/no-v-html -->
    <div v-html="customIconsSprite" />

    <div v-if="loading">
      <skeleton-loader />
    </div>
    <div v-else-if="snServices">
      <h1>Abfrageergebnis</h1>
      <div>
        <div
          class="snServiceElement"
          v-for="service in snServices"
          :key="service.id"
        >
          <div>
            <b>{{ service.serviceName }} (ID: {{ service.id }})</b><br />
            <span>{{ service.summary }}</span
            ><br /><br />
            <div v-if="service.onlineServices">
              <b>Online-Services:</b>
              <ul>
                <li
                  v-for="onlineService in service.onlineServices"
                  :key="onlineService.uri"
                >
                  <a :href="onlineService.uri">{{ onlineService.label }}</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else>oops something went wrong</div>
  </div>
</template>

<script setup lang="ts">
import type { SNService } from "@/api/servicenavigator/ServiceNavigatorLookup.ts";
import type { ServiceNavigatorResult } from "@/api/servicenavigator/ServiceNavigatorResult.ts";

import customIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/custom-icons.svg?raw";
import mucIconsSprite from "@muenchen/muc-patternlab-vue/assets/icons/muc-icons.svg?raw";
import { onMounted, ref } from "vue";

import SkeletonLoader from "@/components/common/skeleton-loader.vue";
import {
  getAPIBaseURL,
  LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT,
} from "@/util/constants.ts";

const loading = ref(true);
const snServices = ref<SNService[] | null>(null);

onMounted(() => {
  loading.value = true;

  const serviceNavigatorResultString = localStorage.getItem(
    LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT
  );
  if (!serviceNavigatorResultString) {
    console.debug(
      "No Data found in LocalStorage with key ",
      LOCALSTORAGE_KEY_SERVICENAVIGATOR_RESULT
    );
    loading.value = false;
  } else {
    const snResult = JSON.parse(
      serviceNavigatorResultString
    ) as ServiceNavigatorResult;
    const url =
      getAPIBaseURL() +
      "/public/api/backend-service/servicenavigator?ids=" +
      snResult.services.join(",");
    fetch(url)
      .then((resp) => {
        if (resp.ok) {
          resp.json().then((snServicesBody: SNService[]) => {
            snServices.value = snServicesBody;
          });
        } else {
          resp.text().then((errBody) => {
            throw Error(errBody);
          });
        }
      })
      .catch((error) => {
        console.debug(error);
      })
      .finally(() => (loading.value = false));
  }
});
</script>

<style>
@import url("https://assets.muenchen.de/mde/1.0.10/css/style.css");
@import "@muenchen/muc-patternlab-vue/assets/css/custom-style.css";
@import "@muenchen/muc-patternlab-vue/style.css";
</style>

<style scoped>
.snServiceElement {
  padding: 16px 16px;
  margin-top: 32px;
  border: 1px solid lightgray;
  border-radius: 16px;
}
</style>
