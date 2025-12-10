# Java TCP File Verification Server

This project is made to interact with my client-server-frontend project, to be added on my portfolio site. I created TCPServer.java and compiled it into a .jar file to be used by Railway; the server awaits an upload, then calculates raw file bytes, the SHA-256 hash, and returns the information to the frontend. 

This shows that the file is properly able to be uploaded and transfered through the TCP pipeline without data loss. I use a Dockerfile to tell Railway how to compile the server. This is the full backend component made to support the aforementioned frontend.

Feel free to download the random provided test image (test.jpg) if you feel uncomfortable uploading any personal images, but rest easy knowing the file is just used to calculate file size and hash before being discarded.

Made to demonstrate:
- raw TCP networking
- server configuration
- Dockerization
- SHA-256 hash verification

---

Note: Server is configured to listen on port 5050
