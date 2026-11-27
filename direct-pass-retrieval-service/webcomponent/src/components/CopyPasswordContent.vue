<script setup lang="ts">
import { MucBanner, MucButton } from "@muenchen/muc-patternlab-vue";
import { ref } from "vue";

const props = defineProps({
    loading: Boolean,
    directPass: {
        type: String,
        default: "",
    },
    errorNoQueryParam: Boolean,
    errorWrongData: Boolean,
    errorLoadingData: Boolean,
});

const copied = ref(false);

function onClickCopy(): void {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(props.directPass!).then(() => {
            copied.value = true;
        });
    } else {
        const textarea = document.createElement("textarea");
        textarea.textContent = props.directPass!;
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand("copy");
        document.body.removeChild(textarea);
        copied.value = false;
    }

    setTimeout(() => {
        copied.value = false;
    }, 1000);
}

function onClickPrint(): void {
    // remove muenchen.de-Elements from printed window
    let htmlStyleElement = document.createElement("style");
    document.head.appendChild(htmlStyleElement);
    htmlStyleElement.textContent = `
    @media print {
      .m-breadcrumb-component, .m-page-header, .m-page-footer {
        display: none;
      }
    }
  `;
    const prevTitle = document.title;
    document.title = "Passwort_" + new Date().toISOString();

    window.print();

    document.head.removeChild(htmlStyleElement);
    document.title = prevTitle;
}
</script>

<template>
    <div>
        <div v-if="loading">Daten werden geladen...</div>

        <div v-if="errorNoQueryParam">
            <muc-banner
                type="warning"
                variant="content"
            >
                Ungültiger Aufruf. Bitte verwenden Sie den vollständigen Link
                aus der E-Mail.
            </muc-banner>
        </div>

        <div v-if="errorWrongData">
            <muc-banner
                type="warning"
                variant="content"
            >
                Dieser Link ist ungültig. Bitte fordern Sie einen neuen an.
            </muc-banner>
        </div>

        <div v-if="errorLoadingData">
            <muc-banner
                type="warning"
                variant="content"
            >
                Die Daten konnten nicht geladen werden. Bitte versuchen Sie es
                später erneut.
            </muc-banner>
        </div>

        <div
            v-if="
                !loading &&
                !errorNoQueryParam &&
                !errorWrongData &&
                !errorLoadingData
            "
        >
            <p>
                Sie haben das Passwort für Ihr Anliegen erneut angefordert. Über
                den Link in der Eingangsbestätigung Ihres Anliegens und das
                folgende Passwort können Sie Ihr Anliegen einsehen:
            </p>

            <div class="pre">
                {{ directPass }}
            </div>

            <div class="actions">
                <muc-button
                    class="button print"
                    icon="printer"
                    @click="onClickPrint"
                >
                    Passwort drucken
                </muc-button>

                <muc-button
                    class="button copy"
                    variant="secondary"
                    :icon="copied ? 'check' : 'copy'"
                    @click="onClickCopy"
                >
                    {{ copied ? "Passwort kopiert" : "Passwort kopieren" }}
                </muc-button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.pre {
    width: 100%;
    height: 48px;
    line-height: 48px;
    margin-top: 32px;
    margin-bottom: 32px;
    background-color: #e5eef5;
    color: #3a5368;
    font-family:
        Roboto Mono,
        monospace;
    font-weight: bold;
    font-size: 22px;
    text-align: center;
}

.actions {
    display: flex;
}

@media screen and (width <= 768px) {
    .actions {
        flex-direction: column;
        align-items: start;
    }

    .button.print {
        margin-bottom: 16px;
    }
}

@media screen and (width > 768px) {
    .actions {
        flex-direction: row;
    }

    .button.print {
        margin-right: 24px;
    }
}

@media print {
    .actions {
        visibility: hidden;
    }
}
</style>
