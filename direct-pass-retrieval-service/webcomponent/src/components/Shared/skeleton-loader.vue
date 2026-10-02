<script setup lang="ts"></script>

<template>
    <div>
        <div class="prod--wrapper">
            <div class="prod--col prod--details">
                <div class="prod--row prod--name">
                    <span
                        id="productName"
                        class="prod--name-text skeleton-loader"
                    />
                </div>
                <div class="prod--row prod--description">
                    <span
                        id="productId"
                        class="prod--description-text skeleton-loader"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.prod--wrapper {
    display: flex;
    width: 95%;
    margin: 32px 0;
    border: 1px solid #b6b6b6;
    border-radius: 6px;
    padding: 22px 10px;
}

.prod--wrapper .prod--row {
    display: flex;
    flex-direction: row;
}

.prod--wrapper .prod--col {
    display: flex;
    flex-direction: column;
}

.prod--wrapper .prod--img {
    width: 20%;
    margin: 0 15px;
}

.prod--wrapper .prod--img .prod--img-graphic {
    max-height: 100%;
    height: 100%;
    vertical-align: top;
    max-width: 100%;
}

.prod--wrapper .prod--details {
    width: 90%;
    margin-left: 17px;
}

.prod--wrapper .prod--details .prod--name {
    margin-bottom: 3px;
    width: 85%;
    display: block;
    max-width: 100%;
}

.prod--wrapper .prod--details .prod--name .prod--name-para {
    margin: 0 auto;
}

.prod--wrapper .prod--details .prod--name .prod--name-text {
    font-weight: bold;
    font-size: 16px;
    line-height: 23px;
    color: #002877;
    height: 40px;
}

.prod--wrapper .prod--details .prod--description {
    margin-bottom: 13px;
}

.prod--wrapper .prod--details .prod--description .prod--description-text {
    font-size: 13px;
    line-height: 18px;
    color: #666666;
}

.skeleton-loader:empty {
    width: 100%;
    height: 15px;
    display: block;
    background:
        linear-gradient(
            to right,
            rgba(255, 255, 255, 0),
            rgba(255, 255, 255, 0.5) 50%,
            rgba(255, 255, 255, 0) 80%
        ),
        lightgray;
    background-repeat: repeat-y;
    background-size: 50px 500px;
    background-position: 0 0;
    animation: shine 1s infinite;
}

@keyframes shine {
    to {
        background-position: 100% 0;
    }
}
</style>
