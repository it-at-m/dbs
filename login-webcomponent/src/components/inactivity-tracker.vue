<script setup lang="ts">
import { onMounted, onUnmounted, watch } from "vue";
import { useCookies } from "vue3-cookies";

const props = defineProps({
  loggedin: {
    type: Boolean,
    default: false,
  },
  //Für ein Auslaufen der Session müssen wir uns den Zeitstempel der letzten Aktivität merken (in Millis)
  lastActive: {
    type: Date,
    default: new Date(0),
  },
});

const emit = defineEmits<{
  (e: "inactive" | "refresh"): void;
  (e: "update:lastActive", value: number): void;
}>();

onMounted(() => {
  setRefreshInterval();
});

watch(
  () => props.loggedin,
  () => {
    setRefreshInterval();
  }
);

const { cookies } = useCookies();
const INACTIVITY_TIMEOUT_IN_SECONDS = 3600;

let intervalID = parseInt(cookies.get("intervalID"));

// 'mousemove' und 'scroll' werden nicht getrackt
const events = ["mousedown", "keypress", "touchstart"];

window.onbeforeunload = () => {
  cookies.remove("intervalID");
};

onMounted(() => {
  // register event listeners at mount time
  events.forEach((ev) => window.addEventListener(ev, resetLastActive));
  window.addEventListener("load", resetLastActive);
});

onUnmounted(() => {
  events.forEach((ev) => window.removeEventListener(ev, resetLastActive));
  window.removeEventListener("load", resetLastActive);
});

function setRefreshInterval() {
  if (props.loggedin) {
    resetLastActive();
    if (!intervalID) {
      intervalID = window.setInterval(() => {
        const millisSinceLastActive =
          Date.now() - props.lastActive.getMilliseconds();
        if (millisSinceLastActive / 1000 > INACTIVITY_TIMEOUT_IN_SECONDS) {
          setTimeout(inactivityDetected, 2000);
        } else {
          emit("refresh");
        }
      }, 30000);
      console.debug("new intervalID is " + intervalID);
      cookies.set("intervalID", intervalID.toString());
    } else {
      console.debug(
        "found intervalID " + intervalID + ". No new interval set."
      );
    }
  } else {
    clearInterval(intervalID);
  }
}

function resetLastActive() {
  emit("update:lastActive", Date.now());
}

function inactivityDetected() {
  emit("inactive");
}
</script>
