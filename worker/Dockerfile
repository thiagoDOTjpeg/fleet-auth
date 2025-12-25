FROM oven/bun:1

WORKDIR /app

COPY package.json bun.lockb* ./
RUN bun install --frozen-lockfile --production

COPY . .

ENV NODE_ENV=production

CMD ["bun", "run", "src/index.ts"]