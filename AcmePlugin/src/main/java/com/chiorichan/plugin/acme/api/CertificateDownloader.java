package com.chiorichan.plugin.acme.api;

import java.io.File;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import org.apache.commons.lang3.Validate;
import org.bouncycastle.x509.util.StreamParsingException;

import com.chiorichan.http.HttpCode;
import com.chiorichan.plugin.acme.AcmePlugin;
import com.chiorichan.plugin.acme.lang.AcmeException;
import com.chiorichan.plugin.acme.lang.AcmeState;
import com.chiorichan.util.FileFunc;

public class CertificateDownloader
{
	private final String certificateUrl;
	private final AcmeCertificateRequest request;
	private X509Certificate certificate = null;
	private AcmeState state = AcmeState.CREATED;

	public CertificateDownloader( AcmeCertificateRequest request, HttpResponse response ) throws AcmeException, StreamParsingException
	{
		Validate.notNull( response );

		this.certificateUrl = response.getHeaderString( "Location" );
		this.request = request;

		handleRequest( response );
	}

	public CertificateDownloader( AcmeCertificateRequest request, String certificateUrl ) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, AcmeException, StreamParsingException
	{
		Validate.notNull( certificateUrl );

		this.certificateUrl = certificateUrl;
		this.request = request;

		check();
	}

	public boolean check() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, AcmeException, StreamParsingException
	{
		HttpResponse response = AcmeUtils.get( certificateUrl, "application/json" );
		AcmePlugin.INSTANCE.getClient().nonce( response.getHeaderString( "Replay-Nonce" ) );

		return handleRequest( response );
	}

	public X509Certificate getCertificate()
	{
		if ( !isDownloaded() )
			try
			{
				check();
			}
			catch ( KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | AcmeException | StreamParsingException e )
			{
				e.printStackTrace();
			}
		return certificate;
	}

	public String getCertificateUri()
	{
		return certificateUrl;
	}

	private boolean handleRequest( HttpResponse response ) throws AcmeException, StreamParsingException
	{
		if ( response.getStatus() == HttpCode.HTTP_CREATED || response.getStatus() == HttpCode.HTTP_OK )
		{
			if ( response.getBody().length > 0 )
			{
				certificate = AcmeUtils.extractCertificate( response.getBody() );

				setState( AcmeState.SUCCESS, "Certificate was successfully received and downloaded" );

				return true;
			}
			else
				setState( AcmeState.PENDING, "The certificate is pending!" );
		}
		else if ( response.getStatus() == HttpCode.HTTP_TOO_MANY_REQUESTS )
			setState( AcmeState.FAILED, "Too many certificates already issued!" );
		else
		{
			response.debug();

			setState( AcmeState.FAILED, "Failed to download certificate" );
		}

		return false;
	}

	public boolean hasFailed()
	{
		return state == AcmeState.INVALID || state == AcmeState.FAILED;
	}

	public boolean isDownloaded()
	{
		return state == AcmeState.SUCCESS && certificate != null;
	}

	/*
	 * public void save() throws AcmeException
	 * {
	 * if ( state != AcmeState.SUCCESS )
	 * throw new IllegalStateException( "Can't save until Certificate Request was successful!" );
	 *
	 * AcmePlugin.INSTANCE.getClient().getAcmeStorage().saveCertificate( domains, certificate );
	 * AcmePlugin.INSTANCE.getClient().getAcmeStorage().saveCertificationRequest( domains, signingRequest );
	 * }
	 */

	public boolean isPending()
	{
		return state == AcmeState.PENDING || state == AcmeState.CREATED;
	}

	public boolean save( File parentDir ) throws AcmeException
	{
		if ( !isDownloaded() )
			return false;

		FileFunc.patchDirectory( parentDir );
		AcmePlugin.INSTANCE.getClient().getAcmeStorage().saveCertificate( parentDir, getCertificate() );

		if ( request != null )
			request.saveCSR( parentDir );

		return true;
	}

	private void setState( AcmeState state, String message )
	{
		this.state = state;
		if ( request != null )
			request.setState( state );
	}
}
