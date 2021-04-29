FROM reactivecircus/android-sdk-30:latest

# Install packages
RUN apt-get -qqy update && \
    apt-get -qqy --no-install-recommends install libc++1 \
    curl ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Open ADB port
EXPOSE 5555
EXPOSE 5556
# Open Firebase emulator ports
EXPOSE 9099
EXPOSE 8080

# Install system images
ENV ARCH=x86 \
    TARGET=google_apis \
    EMULATOR_API_LEVEL=30

# API 30 system image
RUN sdkmanager --install "system-images;android-${EMULATOR_API_LEVEL};${TARGET};${ARCH}" \
    "platforms;android-${EMULATOR_API_LEVEL}" \
    "emulator"

# Install Firebase Emulators
RUN apt-get update && \
      apt-get -y install sudo
RUN curl -sL https://firebase.tools | bash
RUN firebase --version