<template>
  <div v-if="isLoggedIn && userProperties">
    <h2 class="muc-dropdown-header muc-dropdown-border-bottom">
      <span class="visually-hidden"> Mein Bereich: </span>
      <span class="muc-dropdown-header-headline">
        {{ userProperties.personname }}
      </span>
      <span v-if="userProperties.additionalInfoOrganization">
        {{ userProperties.additionalInfoOrganization }}
      </span>
    </h2>
    <nav aria-label="Mein Bereich Navigation">
      <ul style="list-style: none; padding-left: 0; margin-bottom: 0">
        <li>
          <muc-link
            v-if="links?.overviewLink"
            class="muc-dropdown-element muc-dropdown-first-link muc-dropdown-border-bottom"
            tabindex="0"
            target="_self"
            label="Zu meiner Übersicht"
            :href="links.overviewLink"
            no-underline
          />
        </li>
        <li class="dropdown-menu__item">
          <muc-link
            v-if="links?.ticketLink"
            class="muc-dropdown-element muc-dropdown-link"
            tabindex="0"
            target="_self"
            label="Anliegen"
            :href="links.ticketLink"
            no-underline
          />
        </li>
        <li class="dropdown-menu__item">
          <muc-link
            v-if="links?.appointmentLink"
            class="muc-dropdown-element muc-dropdown-link"
            tabindex="0"
            target="_self"
            label="Termine"
            :href="links.appointmentLink"
            no-underline
          />
        </li>
        <li class="dropdown-menu__item">
          <muc-link
            v-if="links?.checklistLink"
            class="muc-dropdown-element muc-dropdown-link"
            tabindex="0"
            target="_self"
            label="Checklisten"
            :href="links.checklistLink"
            no-underline
          />
        </li>
      </ul>
    </nav>
    <div class="muc-dropdown-footer muc-dropdown-border-top">
      <p class="muc-dropdown-footer-text">
        Angemeldet mit {{ userProperties.accountSource }}
        <span v-if="userProperties.authLevel">
          <br />
          ({{ userProperties.authLevel }})
        </span>
      </p>
      <muc-button
        icon="sign-out"
        variant="ghost"
        @click="emit('click-sign-out')"
      >
        <span> Abmelden </span>
      </muc-button>
    </div>
  </div>
  <div v-else>
    <div class="muc-dropdown-header muc-dropdown-border-bottom">
      <p class="muc-dropdown-header-headline">Mein Bereich</p>
    </div>
    <div class="muc-dropdown-login-element">
      <muc-button
        class="muc-dropdown-login-button"
        icon="sing-in"
        variant="primary"
        @click="emit('click-sign-in')"
      >
        <span> Anmelden </span>
      </muc-button>
    </div>
  </div>
</template>
<script setup lang="ts">
import type LinkProperties from "@/types/LinkProperties.ts";
import type UserProperties from "@/types/UserProperties.ts";

import { MucButton, MucLink } from "@muenchen/muc-patternlab-vue";

defineProps<{
  isLoggedIn: boolean;
  userProperties?: UserProperties;
  links?: LinkProperties;
}>();

const emit = defineEmits(["click-sign-in", "click-sign-out"]);
</script>

<style scoped>
/* Header styles */
.muc-dropdown-header {
  display: block;
}

.muc-dropdown-header-headline {
  font-family: "Roboto Condensed", Arial, sans-serif;
  font-size: 1.25rem;
  font-weight: 700;
  line-height: 1.25;
}

/* First dropdown link styles */
.muc-dropdown-first-link {
  font-weight: 700;
}

/* Dropdown element styles */
.muc-dropdown-element {
  background: white;
  display: block;
}

/* Hover effects of clickable dropdown elements */
.muc-dropdown-element:hover {
  background: var(--color-neutrals-blue-xlight);
}

/* Text color  */
.muc-dropdown-content p {
  color: var(--color-neutrals-grey);
}

/* Dropdown login element styles */
.muc-dropdown-login-element {
  background: white;
  width: 100%;
  padding: 16px 24px;
}

/* Dropdown login button styles */
.muc-dropdown-login-button {
  justify-content: center;
  width: 100%;
}

/* Footer styles */
.muc-dropdown-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.muc-dropdown-footer-text {
  font-size: 0.85rem;
}

/* Padding mobile */
.muc-dropdown-header,
.muc-dropdown-first-link {
  padding: 16px;
}

.muc-dropdown-footer,
.muc-dropdown-link {
  padding: 12px 16px;
}

/* Border bottom style */
.muc-dropdown-border-bottom {
  border-bottom: 1px solid var(--color-neutrals-blue) !important;
}

/* Border top style */
.muc-dropdown-border-top {
  border-top: 1px solid var(--color-neutrals-blue) !important;
}

/* No color change on hover over links */
.m-link:hover {
  color: #005a9f !important;
}

/* CSS for desktop */
@media (min-width: 768px) {
  .muc-dropdown-content {
    height: auto;
  }

  .muc-dropdown-header,
  .muc-dropdown-footer {
    padding: 16px 32px;
  }

  .muc-dropdown-first-link {
    padding: 20px 32px;
  }

  .muc-dropdown-link {
    padding: 12px 32px;
  }

  .muc-dropdown-header-headline {
    font-size: 1.5rem;
  }

  .muc-dropdown-footer-text {
    font-size: 1rem;
  }
}

.close-button {
  width: 52px;
  height: 52px;
}
</style>
